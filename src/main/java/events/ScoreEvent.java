package events;

import Entities.Player;
import Managers.MatchManager;
import games.GameX01;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;


public class ScoreEvent extends ListenerAdapter{
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        int points;
        try{
            points = Integer.parseInt(event.getMessage().getContentRaw());
        } catch (NumberFormatException e) {
            return;
            // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
        }
        if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
            // check if a match/game is currently running
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                //check if user is a player of the match
                for(Player p : m.getPlayers()){
                    if (event.getAuthor().getId().equals(p.getId())) {
                        //get game and score
                        GameX01 game = m.getCurrentGame();
                        if (m.isWaitingForStart()){
                            m.startMatch(points,event.getAuthor());
                        } else if (game != null) {
                            game.score(points, event.getAuthor());
                        } else {
                            event.getChannel().sendMessage(
                                    new EmbedBuilder().setDescription("The leg cannot be continued due to an error. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                            ).queue();
                            System.err.println("No leg found in match " + m);
                        }
                        return;
                    }
                }
            } else {
                // Match not in hashmap, bot restarted?
                event.getChannel().sendMessage(
                        new EmbedBuilder().setDescription("There's no match currently running in this channel. Has the Darts-Bot been restarted lately?").setColor(Color.red).build()
                ).queue();
            }
        }
    }
}

