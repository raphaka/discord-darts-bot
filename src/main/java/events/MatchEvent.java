package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MatchEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] messageSent = event.getMessage().getContentRaw().split(" ");
        // ignore message if it doesn't start with !bestof
        if (!messageSent[0].equalsIgnoreCase("!bestof")) {
            return;
        }
        // prompt if too few arguments or second arg is not Integer
        if (messageSent.length < 2){
            event.getChannel(). sendMessage("Please set the number of legs with !bestof <legs> @<opponent>").queue();
            return;
        }
        int legs;
        try{
            legs = Integer.parseInt(messageSent[1]);
        } catch (NumberFormatException e) {
            event.getChannel(). sendMessage("Please set the number of legs with !bestof <legs> @<opponent>").queue();
            return;
            // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
        }
        // prompt if no opponent is chosen
        List<User> mentioned = event.getMessage().getMentionedUsers();
        int numPlayers = mentioned.size();
        List<User> players = new ArrayList<User>();
        if (numPlayers > 0) {
            players.add(event.getMessage().getAuthor());
            players.addAll(mentioned);
        } else {
            event.getChannel().sendMessage("You have to choose at least one opponent. Challenge the other user with !gameon @<username>").queue();
            return;
        }

        if (MatchManager.getInstance().getMatchByChannel(event.getChannel())==null) {
            // todo prompt in Match-/Game objects printed sent before this prompt
            Match m = new Match(event.getChannel(), players, legs);
            MatchManager.getInstance().addMatch(m.getChannel(), m);
            //Send message about players and channel of this game
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("New match started: \n<@").append(event.getMessage().getAuthor().getId());
            for (User u : mentioned) {
                stringBuilder.append("> vs <@").append(u.getId()).append(">");
            }
            stringBuilder.append("\n").append("<#").append(m.getChannel().getId()).append(">");
            event.getChannel().sendMessage(stringBuilder.toString()).queue();
        } else {
            event.getChannel().sendMessage("A match is currently running in this channel. Please wait for the current game to finish.").queue();
        }
    }
}
