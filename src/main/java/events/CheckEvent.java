package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();

        GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
        if (game == null) {
            // Game not in hashmap, bot restarted? todo add persistence for games
            if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
                event.getChannel().sendMessage("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
            }
        } else {
            if (msg.equalsIgnoreCase("check1") || msg.equalsIgnoreCase("check 1") || msg.equalsIgnoreCase("c1")) {
                game.check(1, event.getMessage().getAuthor());
            } else if (msg.equalsIgnoreCase("check2") || msg.equalsIgnoreCase("check 2") || msg.equalsIgnoreCase("c2")) {
                game.check(2, event.getMessage().getAuthor());
            } else if (msg.equalsIgnoreCase("check3") || msg.equalsIgnoreCase("check 3") || msg.equalsIgnoreCase("c3")) {
                game.check(3, event.getMessage().getAuthor());
            }
        }
    }
}
