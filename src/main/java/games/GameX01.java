package games;

import Entities.Player;
import Managers.MatchManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;


public class GameX01 {

    private final TextChannel channel;
    private final HashMap<Player, Integer> scores = new  HashMap<>();
    private int intNextPlayer;
    private final List<Player> players;
    private final int startScore;

    /*
     * Constructor sets the channel to be used.
     * The category of the channel is Dartboard (will be created if not existing)
     * A new channel in this category will be created or an old one will be used
     */
    public GameX01(TextChannel t, List<Player> players, int starter, int startScore){
        this.channel = t;
        this.players = players;
        this.startScore = startScore;
        // set startscore for all players
        for (Player player : players) {
            this.scores.put(player, startScore);
        }
        intNextPlayer = starter;
        Player nextPlayer = players.get(intNextPlayer);
        channel.sendMessage("A new leg started. Score: ").append(String.valueOf(startScore)).append("\n")
                .append(nextPlayer.getName()).append(" to throw first.\nGame on.").queue();
    }

    /*
     * Substracts the scored points from the remaining
     * prompts user to use check command if remaining would be set to 0
     * validates score to avoid impossible values
     */
    public void score(int points, User u){
        // determine who throws next
        Player nextPlayer = players.get(intNextPlayer);
        if (u.getId().equals(nextPlayer.getId())){
            int remaining = scores.get(nextPlayer);
            // Check if points is valid and substract from current score if not checkout or overthrown.
            if( points>180 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible-> impossible == points) ){
                channel.sendMessage("This score cannot be achieved with three darts. Please submit the correct value.").queue();
            } else {
                if (points == remaining) {
                    channel.sendMessage("Finish the game by typing \"check\" and the number of darts needed, e.g. \"check 2.\n " +
                            "You can also type \"c1\", \"c2\" or \"c3\" to save time.").queue();
                } else if (points > remaining || points == remaining-1) {
                    MessageAction msg = channel.sendMessage("Busted! <@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(remaining)).append(" left.");
                    determineNextPlayer();
                    nextPlayer = players.get(intNextPlayer);
                    msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(scores.get(nextPlayer))).append(".").queue();
                } else {
                    scores.put(nextPlayer, scores.get(nextPlayer) - points);
                    MessageAction msg = channel.sendMessage("<@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(scores.get(nextPlayer))).append(" left.");
                    determineNextPlayer();
                    nextPlayer = players.get(intNextPlayer);
                    msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(scores.get(nextPlayer))).append(".").queue();
                }
            }
        } else {
            channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
        }
    }

    /*
     * Basically the same as the scoring function
     */
    public void remaining(int rem, User u){
        Player nextPlayer = players.get(intNextPlayer);
        if (u.getId().equals(nextPlayer.getId())){
            int points = scores.get(nextPlayer) - rem;
            if (points < 0 || points>180 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible-> impossible == points)){
                channel.sendMessage("Your score of ").append(String.valueOf(points)).append(" cannot be achieved with three darts. Please correct your input.").queue();
            } else if (rem == 1 || rem <0){
                MessageAction msg = channel.sendMessage("Busted! <@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(rem)).append(" left.");
                determineNextPlayer();
                nextPlayer = players.get(intNextPlayer);
                msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(scores.get(nextPlayer))).append(".").queue();
            } else if(rem == 0){
                channel.sendMessage("Finish the game by typing \"check\" and the number of darts needed, e.g. \"check 2.\n " +
                        "You can also type \"c1\", \"c2\" or \"c3\" to save time.").queue();
            } else {
                scores.put(nextPlayer, rem);
                MessageAction msg = channel.sendMessage("<@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(rem)).append(" left.");
                determineNextPlayer();
                nextPlayer = players.get(intNextPlayer);
                msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(scores.get(nextPlayer))).append(".").queue();
            }
        } else {
            channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
        }
    }

    // end game with checkout
    // validate if score is possible with the given amount of darts
    public void check(int darts, User u){
        Player nextPlayer = players.get(intNextPlayer);
        if (u.getId().equals(nextPlayer.getId())){
            int rem = scores.get(nextPlayer);
            if (darts == 1 && rem<41 && rem%2==0){
                channel.sendMessage("GAME SHOT by <@").append(nextPlayer.getId()).append("> with the first dart").queue();
                MatchManager.getInstance().getMatchByChannel(channel).playerWonLeg(nextPlayer);
            } else if (darts == 2 && (rem<99 || Arrays.stream(new int[]{100,101,104,107,110}).anyMatch(notbogey-> notbogey == rem))){
                channel.sendMessage("GAME SHOT by <@").append(nextPlayer.getId()).append("> with the second dart").queue();
                MatchManager.getInstance().getMatchByChannel(channel).playerWonLeg(nextPlayer);
            } else if (darts == 3 && (rem<159 || Arrays.stream(new int[]{170,167,164,161,160}).anyMatch(notbogey-> notbogey == rem))){
                channel.sendMessage("GAME SHOT by <@").append(nextPlayer.getId()).append("> with the third dart").queue();
                MatchManager.getInstance().getMatchByChannel(channel).playerWonLeg(nextPlayer);
            } else {
                channel.sendMessage("Your score of ").append(String.valueOf(rem)).append(" cannot be finished with ")
                        .append(String.valueOf(darts)).append(" darts.").queue();
            }
            // prompt user to quit or rematch
            // rematch sets score back to startscore and legcounter ++ for winner
        }else {
            channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
        }
    }

    //corrects the players score.
    //Corrected Score is not validated mathematically
    public void correction(int cor, User u) {
        Player p = getPlayerByUser(u);
        if (cor > 1 && cor < startScore){
            scores.put(p,cor);
            MessageAction msg = channel.sendMessage("Score has been updated manually:\n");
            for (Player player : players) {
                msg = msg.append(player.getName()).append(":  ").append(String.valueOf(scores.get(player))).append("\n");
            }
            Player nextPlayer = players.get(intNextPlayer);
            msg.append("<@").append(nextPlayer.getId()).append("> to throw next.").queue();
        } else {
            channel.sendMessage("Score could not be updated manually.").queue();
        }
    }

    private Player getPlayerByUser(User user){
        for (Player player : players) {
            if(player.getId().equals(user.getId())){
                return player;
            }
        }
        return null;
    }

    //count from player 0 to last player, then start again
    private void determineNextPlayer(){
        if (++intNextPlayer >= this.scores.keySet().toArray().length){
            intNextPlayer = 0;
        }
    }
}

