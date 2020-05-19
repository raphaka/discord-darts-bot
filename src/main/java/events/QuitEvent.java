package events;

import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 * The match can be aborted by the matchs's players and admins and referees
 */
public class QuitEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")){

            //set to true if the game is found. If the game is quit during this function, there will be no prompt that no game has been found
            boolean gameExisted = false;

            //loop through admins, if author is admin: quit match
            Match match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                //check if role ADMIN exists and if member with role exists
                if (event.getGuild().getRolesByName("Admin", true).size() != 0) {
                    event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Admin", true).get(0)).forEach(m -> {
                        if (event.getAuthor() == m.getUser()) {
                            MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                            event.getChannel().sendMessage("Match has been quit.\nUse !gameon or !bestof to start a new match.").queue();
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
                    event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Referee", true).get(0)).forEach(m -> {
                        if (event.getAuthor() == m.getUser()) {
                            MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                            event.getChannel().sendMessage("Match has been quit.\nUse !gameon or !bestof to start a new match.").queue();
                        }
                    });
                }
                gameExisted = true;
            }

            //loop through players of current game, if author is player: quit
            match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                match.getPlayers().forEach(p -> {
                    if (event.getAuthor().getId().equals(p.getId())) {
                        MatchManager.getInstance().removeMatchByChannel(event.getChannel());
                        event.getChannel().sendMessage("Match has been quit. A new match can be started with !gameon or !go followed by @-mentions of your opponents").queue();
                    }
                });
                gameExisted = true;
            }

            //prompt premission error if game couldn't be quit by admin or player
            match = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (match != null) {
                event.getChannel().sendMessage("Only the players of the current match and members of the role \"Admin\" or \"Referee\" are allowed to quit this match.").queue();
            } else {
                if (!gameExisted) {
                    event.getChannel().sendMessage("There is currently no match running").queue();
                }
            }
        }
    }
}
