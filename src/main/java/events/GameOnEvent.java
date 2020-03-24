package events;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class GameOnEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String[] messageSent = event.getMessage().getContentRaw().split(" ");
        if (messageSent[0].equalsIgnoreCase("!gameon")){
            List<User> mentioned = event.getMessage().getMentionedUsers();
            int numPlayers = mentioned.size();
            if (numPlayers > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Playing a game of 501 double out\n");
                stringBuilder.append(event.getMessage().getAuthor().getName());
                for(User u : mentioned){
                    stringBuilder.append(" vs ").append(u.getName());
                }
                event.getChannel().sendMessage(stringBuilder.toString()).queue();
                //TODO: list add 0, author
                //TODO: if not existing for this channel: new gameX01(mentioned, startscore)
                //TODO: create new text channel "N: X vs Y" for every game
            } else {
                event.getChannel().sendMessage("You have to choose at least one opponent. Challenge the other user with !gameon @<username>").queue();
            }
        }
    }
}

//TODO: create gameX01 class