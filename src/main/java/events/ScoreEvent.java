package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class ScoreEvent extends ListenerAdapter{
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        int points;
        try{
            points = Integer.parseInt(event.getMessage().getContentRaw());
        } catch (NumberFormatException e) {
            return;
            // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
        }
        // check if a game is currently running
        GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
        if (game != null) {
            game.score(points, event.getMessage().getAuthor());
        } else {
            // Game not in hashmap, bot restarted? todo add persistence for games
            if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
                event.getChannel().sendMessage("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
            }
        }
    }
}

