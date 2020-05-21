package games;

import Entities.Player;
import Managers.MatchManager;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;

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
        this.channel = useCreateDartboard(t);
        if (this.channel == null){
            t.sendMessage("Match could not be created. No channel found and server limit reached.").queue();
            return;
        }
        MatchManager.getInstance().addMatch(this.channel, this);
        this.num_of_legs = n_o_legs;
        MessageAction msg = t.sendMessage("A new match started.\nChannel: <#").append(channel.getId()).append(">\nPlayers: ");
        for (Player player : players) {
            this.legs.put(player, 0);
            msg = msg.append("<@").append(player.getId()).append("> ");
        }
        msg.append("\nNumber of Legs: ").append(String.valueOf(n_o_legs)).queue();
        channel.sendMessage("A new leg is about to start. Type your score to begin as the first player." +
                "\nType '!random' to let the bot choose who begins." +
                "\nType '!startscore' to play with a different score than 501.").queue();
    }

    //updates legs count for player, determines if/how game is finished + quits match or starts new leg
    public void playerWonLeg(Player winner){
        finished_legs ++;
        legs.put(winner, legs.get(winner) + 1);
        MessageAction msg = channel.sendMessage("Won Legs: \n");
        boolean draw = true; // assume draw until any player in loop has different score than required for draw
        for (Player player : players) {
            msg = msg.append(player.getName()).append(":   ").append(String.valueOf(legs.get(player))).append("\n");
            if (legs.get(player) != num_of_legs/players.size()){
                draw = false;
            }
        }
        msg.queue();

        if (legs.get(winner) > num_of_legs/2.0){
            channel.sendMessage(winner.getName()).append(" has won the match.").queue();
            MatchManager.getInstance().removeMatchByChannel(channel);
            return;
        }

        if (draw){
            channel.sendMessage("It's a draw.").queue();
            MatchManager.getInstance().removeMatchByChannel(channel);
            return;
        }

        if (finished_legs == num_of_legs){
            channel.sendMessage("Match has finished.").queue();
            MatchManager.getInstance().removeMatchByChannel(channel);
            return;
        }

        determineNextPlayer();
        //starter as param
        curGame = new GameX01(channel, this.players, intNextPlayer, startScore);
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
                curGame = new GameX01(channel, players, intNextPlayer,startScore);
            } else {
                curGame = new GameX01(channel, players, intNextPlayer,startScore);
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
