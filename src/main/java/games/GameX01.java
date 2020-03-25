package games;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class GameX01 {

    private TextChannel channel;
    private HashMap<User, Integer> scores = new  HashMap<User, Integer>();

    /*
     * Constructor sets the channel to be used.
     * The category of the channel is Dartboard (will be created if not existing.
     * A new channel in this category will be created or an old one will be used
     */
    public GameX01(TextChannel t, List<User> players){
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
            this.channel = t.getGuild().createTextChannel("Dartboard " + dartboardNumber).setParent(catDartboards).complete();
        }
        // set startscore for all players
        for (User player : players) {
            this.scores.put(player, 501);
        }
        //todo mechanism for choosing who begins
        Random R = new Random();
        User first = (User)scores.keySet().toArray()[R.nextInt(scores.keySet().toArray().length)];
        channel.sendMessage("Starting a new game of 501. <@").append(first.getId()).append("> to throw first.\nGame on.").queue();
    }

    public TextChannel getChannel(){
        return this.channel;
    }

}

//player score avg highscore doubles
