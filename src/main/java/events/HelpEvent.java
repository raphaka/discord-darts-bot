package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.File;
import java.util.Scanner;

public class HelpEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equalsIgnoreCase("!help")){
            event.getChannel().sendMessage("Available commands: \n" +
                    "!gameon @opponent - play a single game of 501 Double out against @opponent (alias: !go)\n" +
                    "!bestof <legs> @opponent - play a match with multiple legs against @opponent (aliases: !bo, !match, !m)\n" +
                    "XY - you scored XY points\n" +
                    "remaining XY - your remaining score is XY points (aliases: r XY, rest XY)\n" +
                    "check Y - you checked out with Y darts (alias: c Y)\n" +
                    "!quit - abandon the current match").queue();
        }
    }
}
