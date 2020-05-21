package events;

import Entities.Player;
import Managers.MatchManager;
import games.Match;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
            event.getChannel().sendMessage("Start score cannot be set to ").append(msg[1]).queue();
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
                            if (stsc == 42 || stsc == 69 || stsc == 420 || stsc == 1337 || stsc == 80085) {
                                event.getChannel().sendMessage("NICE :)").queue();
                            }
                            m.setStartScore(stsc);
                            event.getChannel().sendMessage("Start score has been set to ").append(String.valueOf(stsc)).queue();
                        } else {
                            event.getChannel().sendMessage("Start score cannot be set to ").append(String.valueOf(stsc)).queue();
                        }
                    } else {
                        event.getChannel().sendMessage("The start score can only be set before the match ").queue();
                    }
                } else {
                    event.getChannel().sendMessage("Only players of this match and users with the role \"Referee\" are allowed to set the start score").queue();
                }
            }
        }
    }
}
