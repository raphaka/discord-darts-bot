package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;


public class HelpEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getMessage().getContentRaw().equalsIgnoreCase("!help")){
            EmbedBuilder eb = new EmbedBuilder().setColor(Color.white)
                    .addField("!gameon @opponent","play a single game of 501 Double out against @opponent (alias: !go)",false)
                    .addField("!bestof <legs> @opponent", "play a match with multiple legs against @opponent (aliases: !bo, !match, !m)", false)
                    .addField("<number>", "you scored <number> points", false)
                    .addField("remaining <number>", "your remaining score is <number> points (aliases: r <number>, rest <number>)", false)
                    .addField("check <number>", "you checked out with Y darts (alias: c Y)",false)
                    .addField("correct <number>", "correct the value of your last throw to <number>", false)
                    .addField("!quit", "abandon the current match (alias: !stop)", false)
                    .setTitle("Available commands");
            event.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
