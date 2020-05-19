package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RemainingEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg.length < 2){
            return;
        }
        if (msg[0].equalsIgnoreCase("remaining") || msg[0].equalsIgnoreCase("rest") || msg[0].equalsIgnoreCase("r")) {
            int rem;
            try{
                rem = Integer.parseInt(msg[1]);
            } catch (java.lang.NumberFormatException e) {
                return;
                // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
            }
            // check if a match/game is currently running
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                GameX01 game = m.getCurrentGame();
                if (game != null) {
                    game.remaining(rem, event.getMessage().getAuthor());
                } else {
                    event.getChannel().sendMessage("The game cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
                    System.err.println("No game found in match " + m);
                }
            } else {
                // Match not in hashmap, bot restarted?
                if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
                    event.getChannel().sendMessage("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
                }
            }
        }
    }
}
