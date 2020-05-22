package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class CorrectEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg.length < 2){
            return;
        }
        if (msg[0].equalsIgnoreCase("correct") || msg[0].equalsIgnoreCase("correction") || msg[0].equalsIgnoreCase("cor")) {
            int cor;
            try{
                cor = Integer.parseInt(msg[1]);
            } catch (java.lang.NumberFormatException e) {
                return;
                // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
            }
            // check if a match/game is currently running
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                GameX01 game = m.getCurrentGame();
                if (game != null) {
                    game.correction(cor, event.getMessage().getAuthor());
                } else {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("The leg cannot be continued due to an error. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                    ).queue();
                    System.err.println("No leg found in match " + m);
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
