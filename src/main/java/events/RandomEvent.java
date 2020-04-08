package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RandomEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        // ignore message if not !random
        if (!event.getMessage().getContentRaw().equalsIgnoreCase("!random")) {
            return;
        }
        if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                if (m.isWaitingForStart()){
                    m.startMatch(-1,event.getAuthor());
                }
            }
        }
    }
}
