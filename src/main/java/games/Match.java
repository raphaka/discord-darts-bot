package games;

import Entities.Player;
import Managers.MatchManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class Match {

    private final TextChannel channel;
    private GameX01 curGame;
    private final List<Player> players;
    private int intNextPlayer;
    private boolean waitingForStart = true;
    private float num_of_legs = 1; //float because it is divided by 2 or num of players in playerWonLeg()
    private int finished_legs = 0;
    private final HashMap<Player, Integer> legs = new HashMap<>();
    private int startScore = 501;

    public Match(TextChannel t, List<User> users, int n_o_legs) {
        this.players = new ArrayList<>();
        for (User u : users){
            this.players.add(new Player(u));
        }
        //this.channel is where the match takes place
        //t is the channel from which the match was started
        this.channel = useCreateDartboard(t);
        if (this.channel == null){
            t.sendMessage(
                    new EmbedBuilder().setDescription("Match could not be created. No channel found and server limit reached.").setColor(Color.red).build()
            ).queue();
            return;
        }
        MatchManager.getInstance().addMatch(this.channel, this);
        this.num_of_legs = n_o_legs;
        String instructions = "Type your score to begin as the first player." +
                "\nType '!random' to let the bot choose who begins." +
                "\nType '!startscore' to play with a different score than 501.";
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.blue).setTitle("A new match started");
        StringBuilder sb = new StringBuilder();
        for (Player player : players) {
            this.legs.put(player, 0);
            player.initMatch();
            sb.append("<@").append(player.getId()).append("> ");
        }
        eb.addField("Players",sb.toString(),false);
        eb.addField("Number of Legs",String.valueOf(n_o_legs),false);
        //append start instructions to embed if started from dartboard channel
        //create separate embed in dartboard channel if started from other channel
        if(t == this.channel){
            eb.appendDescription(instructions);
        } else {
            eb.addField("Channel", "<#" + channel.getId() + ">", false);
            t.sendMessage(eb.build()).queue();
            eb.appendDescription(instructions);
            eb.getFields().remove(2);
        }
        channel.sendMessage(eb.build()).queue();

    }

    //updates legs count for player, determines if/how game is finished + quits match or starts new leg
    public void playerWonLeg(Player winner, int darts){
        finished_legs ++;
        legs.put(winner, legs.get(winner) + 1);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.green);
        eb.setTitle("Leg Stats");
        StringBuilder sb = new StringBuilder("Game shot by <@").append(winner.getId());
        if(darts == 1){
            sb.append("> with the first dart");
        }else if(darts == 2){
            sb.append("> with the second dart");
        }else if(darts == 3){
            sb.append("> with the third dart");
        }
        eb.setDescription(sb.toString());
        boolean draw = true; // assume draw until any player in loop has different score than required for draw
        for (Player player : players) {
            //print leg stats
            HashMap<String, Integer> playerLegStats = player.getLegStats();
            double playerAvg = (double)playerLegStats.get("Scored")/playerLegStats.get("Darts")*3;
            String legStatsStr = "Legs: " + legs.get(player) +
                    " | Avg: " + String.format("%.2f", playerAvg) +
                    " | Highest: " + playerLegStats.get("Highest") +
                    " | Darts: " + playerLegStats.get("Darts") +
                    " | 100+: " + playerLegStats.get("100+") +
                    " | 140+: " + playerLegStats.get("140+") +
                    " | 180: " + playerLegStats.get("180");
            eb.addField(player.getName(), legStatsStr, false);
            // determine if a player has different score than required for draw
            if (legs.get(player) != num_of_legs/players.size()){
                draw = false;
            }
            //update matchStats
            player.finishLeg();
        }
        channel.sendMessage(eb.build()).queue();

        if (legs.get(winner) > num_of_legs/2.0){
            finishMatch(winner, false);
            return;
        }

        if (draw){
            finishMatch(null, true);
            return;
        }

        //match with more than two players finished
        if (finished_legs == num_of_legs){
            finishMatch(null, false);
            return;
        }

        determineNextPlayer();
        //starter as param
        curGame = new GameX01(channel, this.players, intNextPlayer, startScore);
    }

    private void finishMatch(Player winner, boolean draw){
        if(num_of_legs > 1) {
            EmbedBuilder eb = new EmbedBuilder().setColor(Color.blue).setTitle("Match Stats");
            if(draw){
                eb.setDescription("It's a draw.");
            }else{
                if (winner != null){
                    eb.setDescription(winner.getName()).appendDescription("won the match.");
                } else {
                    eb.setDescription("Match has finished.");
                }
            }
            for (Player p : this.players) {
                //format MatchStats into embed field
                HashMap<String, Integer> playerMatchStats = p.getMatchStats();
                double playerAvg = (double) playerMatchStats.get("Scored") / playerMatchStats.get("Darts") * 3;
                String matchStatsStr = "Legs: " + legs.get(p) +
                        " | Avg: " + String.format("%.2f", playerAvg) +
                        " | Highest: " + playerMatchStats.get("Highest") +
                        " | Darts: " + playerMatchStats.get("Darts") +
                        " | 100-139: " + playerMatchStats.get("100+") +
                        " | 140-179: " + playerMatchStats.get("140+") +
                        " | 180: " + playerMatchStats.get("180");
                eb.addField(p.getName(), matchStatsStr, false);
            }
            channel.sendMessage(eb.build()).queue();
        }
        MatchManager.getInstance().removeMatchByChannel(channel);
    }

    //create new game and score for the first player
    public void startMatch(int points, User u){
        Player p = getPlayerByUser(u);
        intNextPlayer = players.indexOf(p);
        if (intNextPlayer != -1){
            if (points == -1){
                // random player begins
                Random R = new Random();
                intNextPlayer = R.nextInt(players.size());
                curGame = new GameX01(channel, players, intNextPlayer, startScore);
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.green).setDescription(curGame.getPlayers().get(intNextPlayer).getName()).appendDescription(" to throw first.");
                channel.sendMessage(eb.build()).queue();
            } else {
                curGame = new GameX01(channel, players, intNextPlayer, startScore);
                channel.sendMessage(players.get(intNextPlayer).getName()).append(" scored ").append(String.valueOf(points)).append(" points").queue();
                curGame.score(points,u);
            }
            waitingForStart = false;
        }
    }

    //count from player 0 to last player, then start again
    private void determineNextPlayer(){
        if (++intNextPlayer >= this.legs.keySet().toArray().length){
            intNextPlayer = 0;
        }
    }


    private TextChannel useCreateDartboard(TextChannel t){
        // add category Dartboards if not existing
        if (t.getGuild().getCategoriesByName("Dartboards",true).isEmpty()){
            t.getGuild().createCategory("Dartboards").complete();
        }
        Category catDartboards = t.getGuild().getCategoriesByName("Dartboards",true).get(0);
        // start game in this channel or create a new one
        if (t.getParent() == catDartboards){
            return t;
        } else {
            // Create a new text channel in Category Dartboards or use an unused one
            // loop through all numbers
            int dartboardNumber = 1;
            while (!t.getGuild().getTextChannelsByName("dartboard-" + dartboardNumber, true).isEmpty()) {
                // loop through all boards with the number
                for (TextChannel board : t.getGuild().getTextChannelsByName("dartboard-" + dartboardNumber, true)) {
                    // use this channel if it is currently not used
                    if (!MatchManager.getInstance().getUsedChannels().contains(board)) {
                        return board;
                    }
                }
                dartboardNumber++;
            }
            //create a new channel if possible
            if (t.getGuild().getChannels().size() == 500) {
                // discord server limit is reached
                return null;
            } else {
                return t.getGuild().createTextChannel("dartboard-" + dartboardNumber).setParent(catDartboards).complete();
            }
        }
    }

    private Player getPlayerByUser(User user){
        for (Player player : players) {
            if(player.getId().equals(user.getId())){
                return player;
            }
        }
        return null;
    }

    public GameX01 getCurrentGame(){
        return this.curGame;
    }
    public TextChannel getChannel(){
        return this.channel;
    }
    public Set<Player> getPlayers(){
        return legs.keySet();
    }
    public boolean isWaitingForStart() {
        return waitingForStart;
    }
    public void setStartScore(int s) { this.startScore = s; }

}
