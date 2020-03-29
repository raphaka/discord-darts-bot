package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
        if (messageSent[0].equalsIgnoreCase("!gameon") || messageSent[0].equalsIgnoreCase("!go")){
            // create a list of the participating players
            // players are author and mentioned of the events message
            List<User> mentioned = event.getMessage().getMentionedUsers();
            int numPlayers = mentioned.size();
            if (numPlayers > 0) {
                List<User> players = new ArrayList<User>();
                players.add(event.getMessage().getAuthor());
                players.addAll(mentioned);
                // check if a game is currently active in the channel
                // if not, create a new game for this channel
                // the constructor of GameX01 will check the channel's category
                if (GameManager.getInstance().getGameByChannel(event.getChannel())==null) {
                    GameX01 g = new GameX01(event.getChannel(), players);
                    GameManager.getInstance().addGameX01(g.getChannel(), g);
                    //Send message about players and channel of this game
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Playing a game of 501 double out\n<@").append(event.getMessage().getAuthor().getId());
                    for (User u : mentioned) {
                        stringBuilder.append("> vs <@").append(u.getId()).append(">");
                    }
                    stringBuilder.append("\n").append("<#").append(g.getChannel().getId()).append(">");
                    event.getChannel().sendMessage(stringBuilder.toString()).queue();
                } else {
                    event.getChannel().sendMessage("A game is currently running in this channel. Please wait for the current game to finish.").queue();
                }
            } else {
                event.getChannel().sendMessage("You have to choose at least one opponent. Challenge the other user with !gameon @<username>").queue();
            }  
        }
    }
}
