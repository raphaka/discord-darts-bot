package events;

import Entities.Player;
import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class StartScoreEvent extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        int stsc;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg.length < 2){
            return;
        }
        // ignore message if not !startscore
        if (!msg[0].equalsIgnoreCase("!startscore") && !msg[0].equalsIgnoreCase("!stsc")) {
            return;
        }
        try{
            stsc = Integer.parseInt(msg[1]);
        } catch (java.lang.NumberFormatException e) {
            event.getChannel().sendMessage(
                    new EmbedBuilder().setDescription("Start score cannot be set to ").setColor(Color.red).build()
            ).append(msg[1]).queue();
            return;
            // if message cannot be parsed as an Integer, it is not meant to be processed by this handler
        }
        if (event.getGuild().getCategoriesByName("Dartboards", true).contains(event.getChannel().getParent())) {
            Match m = MatchManager.getInstance().getMatchByChannel(event.getChannel());
            if (m != null) {
                //check if user is allowed to set start score
                boolean allowed = false;
                //check if user is a player of the match
                for(Player p : m.getPlayers()){
                    if (event.getAuthor().getId().equals(p.getId())) {
                        allowed = true;
                    }
                }
                //check if player has role referee
                if (event.getGuild().getRolesByName("Referee", true).size() != 0) {
                    Role referee = event.getGuild().getRolesByName("Referee", true).get(0);
                    for(Member mem : event.getGuild().getMembersWithRoles(referee)){
                        if (event.getAuthor() == mem.getUser()) {
                            allowed = true;
                        }
                    }
                }
                if(allowed) {
                    if (m.isWaitingForStart()) {
                        if (stsc > 1) {
                            //eastereggs
                            if (stsc == 666){ //horn hand gesture emoji
                                event.getChannel().sendMessage(":metal:").queue();
                            } else if (stsc == 42 || stsc == 69 || stsc == 420 || stsc == 1337 || stsc == 80085) {
                                event.getChannel().sendMessage("NICE :)").queue();
                            } else if (stsc == 118999){ //IT crowd
                                event.getChannel().sendMessage("88 199 9119 725").queue();
                                try { Thread.sleep(1500); } catch (InterruptedException e) {e.printStackTrace(); }
                                event.getChannel().sendMessage("threeeee").queue();
                            }
                            m.setStartScore(stsc);
                            event.getChannel().sendMessage(
                                    new EmbedBuilder().setDescription("Start score has been set to " + stsc).setColor(Color.green).build()
                            ).queue();
                        } else {
                            event.getChannel().sendMessage(
                                    new EmbedBuilder().setDescription("Start score cannot be set to " + stsc).setColor(Color.red).build()
                            ).queue();
                        }
                    } else {
                        event.getChannel().sendMessage(
                                new EmbedBuilder().setDescription("The start score can only be set before the match ").setColor(Color.red).build()
                        ).queue();
                    }
                } else {
                    event.getChannel().sendMessage(
                            new EmbedBuilder().setDescription("Only players of this match and users with the role \"Referee\" are allowed to set the start score").setColor(Color.red).build()
                    ).queue();
                }
            }
        }
    }
}
