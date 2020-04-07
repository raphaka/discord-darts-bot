package games;

import Managers.MatchManager;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Match {

    private TextChannel channel;
    private GameX01 curGame;
    private List<User> players;
    private int intNextPlayer;
    private float num_of_legs = 1; //float because it is divided by 2 or num of players in playerWonLeg()
    private int finished_legs = 0;
    private HashMap<User, Integer> legs = new HashMap<User, Integer>();

    public Match(TextChannel t, List<User> p, int n_o_legs) {
        this.players = p;
        this.channel = t;
        useCreateDartboard(t);
        this.num_of_legs = n_o_legs;
        for (User player : players) {
            this.legs.put(player, 0);
        }
       curGame = new GameX01(channel, players);
    }

    //updates legs count for player, determines if/how game is finished + quits match or starts new leg
    public void playerWonLeg(User winner){
        finished_legs ++;
        legs.put(winner, legs.get(winner) + 1);
        MessageAction msg = channel.sendMessage("Won Legs: \n");
        boolean draw = true; // assume draw until any player in loop has different score than required for draw
        for (User player : players) {
            msg.append(player.getName()).append(":   ").append(String.valueOf(legs.get(player))).append("\n");
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
            channel.sendMessage("Game has finished.").queue();
            MatchManager.getInstance().removeMatchByChannel(channel);
            return;
        }

        determineNextPlayer();
        //starter as param
        curGame = new GameX01(channel, this.players);
    }

    //count from player 0 to last player, then start again
    private void determineNextPlayer(){
        if (++intNextPlayer >= this.legs.keySet().toArray().length){
            intNextPlayer = 0;
        }
    }


    private void useCreateDartboard(TextChannel t){
        // add category Dartboards if not existing
        if (t.getGuild().getCategoriesByName("Dartboards",true).isEmpty()){
            t.getGuild().createCategory("Dartboards").complete();
        }
        Category catDartboards = t.getGuild().getCategoriesByName("Dartboards",true).get(0);
        // start game in this channel or create a new one
        if (t.getParent() == catDartboards){
            this.channel = t;
        } else {
            // Create a new text channel in Category Dartboards or use an unused one
            int dartboardNumber = 1;
            while(!t.getGuild().getTextChannelsByName("dartboard-" + dartboardNumber,true).isEmpty()){
                dartboardNumber++;
            }
            this.channel = t.getGuild().createTextChannel("dartboard-" + dartboardNumber).setParent(catDartboards).complete();
        }
    }

    public GameX01 getCurrentGame(){
        return this.curGame;
    }
    public TextChannel getChannel(){
        return this.channel;
    }
    public Set<User> getPlayers(){
        return legs.keySet();
    }

}
