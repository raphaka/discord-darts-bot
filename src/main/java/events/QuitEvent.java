package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

/*
 * The match can be aborted by the matchs's players and admins and referees
 */
public class QuitEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")){

            //set to true if the game is found. If the game is quit during this function, there will be no prompt that no game has been found
            boolean gameExisted = false;

            //create embed message
            EmbedBuilder eb = new EmbedBuilder().setColor(Color.blue).setTitle("Match has been quit.")
                    .setDescription("Use !gameon or !bestof to start a new match.");

            //loop through admins, if author is admin: quit match
            Match match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                //check if role ADMIN exists and if member with role exists
                if (event.getGuild().getRolesByName("Admin", true).size() != 0) {
                    EmbedBuilder finalEb = eb;
                    event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Admin", true).get(0)).forEach(m -> {
                        if (event.getAuthor() == m.getUser()) {
                            MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                            event.getChannel().sendMessage(finalEb.build()).queue();
                        }
                    });
                }
                gameExisted = true;
            }

            //loop through referees, if author is referee: quit match
            match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                //check if role REFEREE exists and if member with role exists
                if (event.getGuild().getRolesByName("Referee", true).size() != 0) {
                    EmbedBuilder finalEb1 = eb;
                    event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Referee", true).get(0)).forEach(m -> {
                        if (event.getAuthor() == m.getUser()) {
                            MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                            event.getChannel().sendMessage(finalEb1.build()).queue();
                        }
                    });
                }
                gameExisted = true;
            }

            //loop through players of current game, if author is player: quit
            match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                EmbedBuilder finalEb2 = eb;
                match.getPlayers().forEach(p -> {
                    if (event.getAuthor().getId().equals(p.getId())) {
                        MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                        event.getChannel().sendMessage(finalEb2.build()).queue();
                    }
                });
                gameExisted = true;
            }

            //prompt premission error if game couldn't be quit by admin or player
            match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                eb = new EmbedBuilder().setDescription("Only the players of the current match and members of the role \"Admin\" or \"Referee\" are allowed to quit this match.").setColor(Color.red);
                event.getChannel().sendMessage(eb.build()).queue();
            } else {
                if (!gameExisted) {
                    eb = new EmbedBuilder().setDescription("There is currently no match running in this channel").setColor(Color.red);
                    event.getChannel().sendMessage(eb.build()).queue();
                }
            }
        }
    }
}
