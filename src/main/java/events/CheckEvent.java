package events;

import Managers.GameManager;
import games.GameX01;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CheckEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // parse if message check, c or checkout
        String msg = event.getMessage().getContentRaw();
        if (msg.toLowerCase().startsWith("check") || msg.toLowerCase().startsWith("c")
                || msg.toLowerCase().startsWith("checkout") || msg.toLowerCase().startsWith("gameshot")) {
            // check if game is currently running in this channel
            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
            if (game == null) {
                // Game not in hashmap, bot restarted? todo add persistence for games
                if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
                    event.getChannel().sendMessage("The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
                }
            // if game is running, call checkout with amount of darts
            } else {
                switch (msg.substring(msg.length() - 1)) {
                    case "1":
                        game.check(1, event.getMessage().getAuthor());
                        break;
                    case "2":
                        game.check(2, event.getMessage().getAuthor());
                        break;
                    case "3":
                        game.check(3, event.getMessage().getAuthor());
                        break;
                }
            }

        }
    }
}


//        if (msg.equalsIgnoreCase("check1") || msg.equalsIgnoreCase("check 1") ||
//                msg.equalsIgnoreCase("c1") || msg.equalsIgnoreCase("c 1")) {
//            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
//            if (game == null) {
//                // Game not in hashmap, bot restarted? todo add persistence for games
//                if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
//                    event.getChannel().sendMessage("check The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
//                }
//            } else {
//                game.check(1, event.getMessage().getAuthor());
//            }
//        } else if (msg.equalsIgnoreCase("check2") || msg.equalsIgnoreCase("check 2") ||
//                msg.equalsIgnoreCase("c2") || msg.equalsIgnoreCase("c 2")) {
//            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
//            if (game == null) {
//                // Game not in hashmap, bot restarted? todo add persistence for games
//                if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
//                    event.getChannel().sendMessage("check The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
//                }
//            } else {
//                game.check(2, event.getMessage().getAuthor());
//            }
//        } else if (msg.equalsIgnoreCase("check3") || msg.equalsIgnoreCase("check 3") ||
//                msg.equalsIgnoreCase("c3") || msg.equalsIgnoreCase("c 3")) {
//            GameX01 game = GameManager.getInstance().getGameByChannel(event.getChannel());
//            if (game == null) {
//                // Game not in hashmap, bot restarted? todo add persistence for games
//                if (event.getGuild().getCategoriesByName("Dartboards",true).contains(event.getChannel().getParent())) {
//                    event.getChannel().sendMessage("check The match cannot be continued due to an error. Has the Darts-Bot been restarted lately?").queue();
//                }
//            } else {
//                game.check(3, event.getMessage().getAuthor());
//            }
//        }
//    }
//}
