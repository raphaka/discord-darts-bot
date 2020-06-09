package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MatchEvent extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();
        String[] keywords = new String[] {"!bestof","!bo","!m ","!match"};
        for(String kw : keywords){
            if (msg.toLowerCase().startsWith(kw)){
                String[] messageSent = msg.toLowerCase().replaceFirst(kw,"").trim().split(" ");

                // prompt if too few arguments
                if (messageSent.length < 2){
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("Please set the number of legs and select at least one opponent with !bestof <legs> @ <opponent>").setColor(Color.red).build()
                    ).queue();
                    return;
                }

                //prompt and exit if legs not parsed as integer
                int legs;
                try{
                    legs = Integer.parseInt(messageSent[0]);
                } catch (NumberFormatException e) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription(messageSent[0] + " is not a valid number of legs. Please set the number of legs with !bestof <legs> @<opponent>").setColor(Color.red).build()
                    ).queue();
                    return;
                    // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
                }

                //validate leg number and easteregg
                if (legs < 1){
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription(messageSent[1] + " is not a valid number of legs. Please set the number of legs with !bestof <legs> @<opponent>").setColor(Color.red).build()
                    ).queue();
                    return;
                }else if(legs > 9000) {
                    event.getChannel().sendMessage(
                            //Over 9000 meme from knowyourmeme.com
                            new EmbedBuilder().setImage("https://i0.kym-cdn.com/entries/icons/original/000/000/056/itsover1000.jpg").build()
                    ).queue();
                }

                // prompt if no opponent is chosen
                List<User> users = new ArrayList<>();
                users.add(event.getMessage().getAuthor());
                for (User u : event.getMessage().getMentionedUsers()){
                    if(!(users.contains(u) || u.isBot())){
                        users.add(u);
                    }
                }

                //check if enough players
                if(users.size() < 2) {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("You have to choose at least one opponent. Challenge the other user with !gameon @<username>\nYou cannot challenge bots or yourself.").setColor(Color.red).build()
                    ).queue();
                    return;
                }

                // Start new match
                if (MatchManager.getInstance().getMatchByChannel(event.getChannel())==null) {
                    new Match(event.getChannel(), users, legs);
                } else {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("A match is currently running in this channel. Please wait for the current match to finish.").setColor(Color.red).build()
                    ).queue();
                }
            }
        }
    }
}
