package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // parse if message check, c or checkout
        String msg = event.getMessage().getContentRaw();
        if (msg.toLowerCase().startsWith("check ") || msg.toLowerCase().startsWith("c ")
                || msg.toLowerCase().startsWith("checkout ") || msg.toLowerCase().startsWith("gameshot")) {
            // check if match/game is currently running in this channel
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                GameX01 game = m.getCurrentGame();
                if (game != null) {
                    switch (msg.substring(msg.length() - 1)) {
                        case "1":
                            game.check(1, event.getMessage().getAuthor());
                            break;
                        case "2":
                            game.check(2, event.getMessage().getAuthor());
                            break;
                        case "3":
                            game.check(3, event.getMessage().getAuthor());
                            break;
                    }
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

