package events;

import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class RemainingEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] keywords = new String[] {"r","rem","rest","remaining"};
        for(String kw : keywords){
            if (msg.toLowerCase().startsWith(kw)){
                int rem;
                try{
                    rem = Integer.parseInt(msg.toLowerCase().replaceFirst(kw,"").trim());
                } catch (java.lang.NumberFormatException e) {
                    continue;
                    // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
                }
                // check if a match/game is currently running
                Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
                if (m != null) {
                    GameX01 game = m.getCurrentGame();
                    if (game != null) {
                        game.remaining(rem, event.getMessage().getAuthor());
                    } else {
                        event.getChannel().sendMessage(
                                new EmbedBuilder().setDescription("There's currently no leg running in this channel. If you just started a match, type your score as the first player as is. The 'remaining' feature can be used as soon as the game starts.").setColor(Color.red).build()
                        ).queue();
                        System.err.println("No leg found in match " + m);
                    }
                } else {
                    // Match not in hashmap, bot restarted?
                    if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
                        event.getChannel().sendMessage(
                                new EmbedBuilder().setDescription("There's no match running in this server. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                        ).queue();
                    }
                }
                return;
            }
        }
    }
}
