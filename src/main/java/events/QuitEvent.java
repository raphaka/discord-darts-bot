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
            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
            if (game != null) {
                //loop through admins, if author is admin: quit game
                // todo admin role/ permission must not be hard coded
                event.getGuild().getMembersWithRoles(event.getGuild().getRoleById("693621926161285121")).forEach(m -> {
                    if (event.getAuthor() == m.getUser()){
                        GameManager.getInstance().removeGameX01ByChannel(event.getChannel());
                        event.getChannel().sendMessage("Game has been quit. A new game can be started with !gameon or !go followed by @-mentions of your opponents").queue();
                        return;
                    }
                });
                //loop through players of current game, if author is player: quit
                GameManager.getInstance().getGameByChannel(event.getChannel()).getPlayers().forEach(u ->{
                    if (event.getAuthor() == u){
                        GameManager.getInstance().removeGameX01ByChannel(event.getChannel());
                        event.getChannel().sendMessage("Game has been quit. A new game can be started with !gameon or !go followed by @-mentions of your opponents").queue();
                        return;
                    }
                });
                event.getChannel().sendMessage("Only the players of the current game and admins are allowed to quit this game.").queue();
            } else {
                event.getChannel().sendMessage("There is currently no game running").queue();
            }
        }
    }
}
