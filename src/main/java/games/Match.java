package games;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Match {

    private TextChannel channel;
    private GameX01 curGame;
    private List<User> players;
    private int intNextPlayer;
    private int num_of_legs = 1;
    private HashMap<User, Integer> legs = new HashMap<User, Integer>();

    public Match(TextChannel t, List<User> p, int n_o_legs) {
        this.players = p;
        System.out.println("match started with " + n_o_legs + "legs");
        this.channel = t;
        useCreateDartboard(t);
        this.num_of_legs = n_o_legs;
        for (User player : players) {
            this.legs.put(player, 0);
        }
       curGame = new GameX01(channel, players);
    }

    public void playerWonLeg(User winner){
        legs.put(winner, legs.get(winner) + 1);
        channel.sendMessage("Won Legs: " + legs.get(winner)).queue();
//        if (this.num_of_legs%2!=0){
//            if (legs.get(winner) <= this.num_of_legs/2 + 0.5){
//                //todo determine if player won match
//            }
//        }
        //todo prompt and remove game if bestof is won
        //determineNextPlayer();
        //User nextPlayer = (User)legs.keySet().toArray()[intNextPlayer];
        //channel.sendMessage("<@").append(nextPlayer.getId()).append("> to throw first").queue();
        determineNextPlayer();
        //todo new game with starter as param
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
