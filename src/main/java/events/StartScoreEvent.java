package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class StartScoreEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        int stsc = 501;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg.length < 2){
            return;
        }
        // ignore message if not !startscore
        if (!msg[0].equalsIgnoreCase("!startscore") && !msg[0].equalsIgnoreCase("!stsc")) {
            return;
        }
        try{
            stsc = Integer.parseInt(msg[1]);
        } catch (java.lang.NumberFormatException e) {
            return;
            // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
        }
        if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                if (m.isWaitingForStart()){
                    if (stsc > 1) {
                        m.setStartScore(stsc);
                        event.getChannel().sendMessage("Start score has been set to ").append(String.valueOf(stsc)).queue();
                    } else {
                        event.getChannel().sendMessage("Start score cannot be set to ").append(String.valueOf(stsc)).queue();
                    }
                }
            }
        }
    }
}
