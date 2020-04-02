package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/*
 * The game can be aborted by the game's players and admins
 */
public class QuitEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().equalsIgnoreCase("!quit")){

            //set to true if the game is found. If the game is quit during this function, there will be no prompt that no game has been found
            boolean gameExisted = false;

            //loop through admins, if author is admin: quit game
            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
            if (game != null) {
                //check if role exists and if member with role exists
                if (event.getGuild().getRolesByName("Admin", true).get(0) != null) {
                    event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Admin", true).get(0)).forEach(m -> {
                        if (event.getAuthor() == m.getUser()) {
                            GameManager.getInstance().removeGameX01ByChannel(event.getChannel());
                            event.getChannel().sendMessage("Game has been quit. A new game can be started with !gameon or !go followed by @-mentions of your opponents").queue();
                        }
                    });
                }
                gameExisted = true;
            }

            //loop through players of current game, if author is player: quit
            game = GameManager.getInstance().getGameByChannel(event.getChannel());
            if (game != null) {
                game.getPlayers().forEach(u -> {
                    if (event.getAuthor() == u) {
                        GameManager.getInstance().removeGameX01ByChannel(event.getChannel());
                        event.getChannel().sendMessage("Game has been quit. A new game can be started with !gameon or !go followed by @-mentions of your opponents").queue();
                    }
                });
                gameExisted = true;
            }


            //prompt premission error if game couldn't be quit by admin or player
            game = GameManager.getInstance().getGameByChannel(event.getChannel());
            if (game != null) {
                event.getChannel().sendMessage("Only the players of the current game and members of the role \"Admin\" are allowed to quit this game.").queue();
            } else {
                if (!gameExisted) {
                    event.getChannel().sendMessage("There is currently no game running").queue();
                }
            }
        }
    }
}
