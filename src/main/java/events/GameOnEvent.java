package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/*
 * If the player types !gameon a new game will start against the mentioned opponents
 * If it is started in a channel in the category "dartboards" the match will start in this channel
 * If it is started from another text channel, it will create a new one in the category dartboards
 */
public class GameOnEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        // parse !gameon message
        String[] messageSent = event.getMessage().getContentRaw().split(" ");
        if (!(messageSent[0].equalsIgnoreCase("!gameon") || messageSent[0].equalsIgnoreCase("!go"))) {
            return;
        }

        // prompt if no opponent is chosen
        List<User> users = new ArrayList<>();
        users.add(event.getMessage().getAuthor());
        for (User u : event.getMessage().getMentionedUsers()){
            if(!(users.contains(u) || u.isBot())){
                users.add(u);
            }
        }
        if(users.size() < 2) {
            event.getChannel().sendMessage(
                    new EmbedBuilder().setDescription("You have to choose at least one opponent. Challenge the other user with !gameon @<username>\nYou cannot challenge bots or yourself.").setColor(Color.red).build()
            ).queue();
            return;
        }

        // Start new match
        if (MatchManager.getInstance().getMatchByChannel(event.getChannel())==null) {
            Match m = new Match(event.getChannel(), users, 1);
            MatchManager.getInstance().addMatch(m.getChannel(), m);
        } else {
            event.getChannel().sendMessage(
                    new EmbedBuilder().setDescription("A match is currently running in this channel. Please wait for the current match to finish.").setColor(Color.red).build()
            ).queue();
        }
    }
}
