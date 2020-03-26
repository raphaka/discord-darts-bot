package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RemainingEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg[0].equalsIgnoreCase("remaining") || msg[0].equalsIgnoreCase("rest") || msg[0].equalsIgnoreCase("r")) {
            int rem;
            try{
                rem = Integer.parseInt(msg[1]);
                GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
                if (game != null) {
                    game.remaining(rem, event.getMessage().getAuthor());
                } else {
                    // Game not in hashmap, bot restarted? todo add persistence for games
                    if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
                        event.getChannel().sendMessage("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
                    }
                }
            } catch (java.lang.NumberFormatException e) {
                // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
            }
        }
    }
}
