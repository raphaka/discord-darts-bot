package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CheckEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // parse if message check, c or checkout
        String msg = event.getMessage().getContentRaw();
        if (msg.toLowerCase().startsWith("check ") || msg.toLowerCase().startsWith("c ")
                || msg.toLowerCase().startsWith("checkout ") || msg.toLowerCase().startsWith("gameshot")
                || msg.equalsIgnoreCase("c1") || msg.equalsIgnoreCase("c2") || msg.equalsIgnoreCase("c3")
                || msg.equals("1") || msg.equals("2") || msg.equals("3") ) {
            // check if match/game is currently running in this channel
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                if(m.hasUser(event.getAuthor())) {
                    GameX01 game = m.getCurrentGame();
                    if (game != null) {
                        //single numbers only trigger checkout if they are expected because the player's score is 0
                        //return if another player types 1,2 or 3
                        if ( (msg.equals("1") || msg.equals("2") || msg.equals("3"))
                                && !game.getWaitingForCheck().getId().equals(event.getAuthor().getId())){
                            return;
                        }
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
                        event.getChannel().sendMessage(
                                new EmbedBuilder().setDescription("The leg cannot be continued due to an error. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                        ).queue();
                        System.err.println("No leg found in match " + m);
                    }
                }
            } else {
                // Match not in hashmap, bot restarted?
                if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                    ).queue();
                }
            }
        }
    }
}

