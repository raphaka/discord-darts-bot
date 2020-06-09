package games;

import Entities.Player;
import Managers.MatchManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;
import java.util.*;
import java.util.List;


public class GameX01 {

    private final TextChannel channel;
    private int intNextPlayer;
    private final List<Player> players;
    private Player waitingForCheck = null;

    /*
     * Constructor sets the channel to be used.
     * The category of the channel is Dartboard (will be created if not existing)
     * A new channel in this category will be created or an old one will be used
     */
    public GameX01(TextChannel t, List<Player> players, int starter, int startScore){
        this.channel = t;
        this.players = players;
        // set startscore for all players
        for (Player player : players) {
            player.initLeg(startScore, 0);
        }
        intNextPlayer = starter;
    }

    /*
     * Substracts the scored points from the remaining if valid
     */
    public void score(int points, User u){
        // determine who throws next
        Player nextPlayer = players.get(intNextPlayer);
        if (u.getId().equals(nextPlayer.getId())){
            // Check if points is valid and substract from current score if not checkout or overthrown.
            if( points>180 || points < 0 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible -> impossible == points) ){
                channel.sendMessage(
                        new EmbedBuilder().setDescription("This score cannot be achieved with three darts. Please submit the correct value.").setColor(Color.red).build()
                ).queue();
            } else {
                int newScore = nextPlayer.score(points);
                validateScore(nextPlayer, newScore, true);
            }
        } else {
            channel.sendMessage(
                    new EmbedBuilder().setDescription("It's <@" + nextPlayer.getId() + ">'s turn to throw. Please wait.").setColor(Color.red).build()
            ).queue();
        }
    }

    /*
     * Basically the same as the scoring function
     */
    public void remaining(int rem, User u){
        Player nextPlayer = players.get(intNextPlayer);
        if (u.getId().equals(nextPlayer.getId())){
            int points = nextPlayer.getCurrentScore() - rem;
            if (points < 0 || points > 180 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible-> impossible == points)){
                channel.sendMessage(
                        new EmbedBuilder().setDescription("This score cannot be achieved with three darts. Please submit the correct value.").setColor(Color.red).build()
                ).queue();
            } else {
                int newScore = nextPlayer.score(points);
                validateScore(nextPlayer, newScore, true);
            }
        } else {
            channel.sendMessage(
                    new EmbedBuilder().setDescription("It's <@" + nextPlayer.getId() + ">'s turn to throw. Please wait.").setColor(Color.red).build()
            ).queue();
        }
    }

    // end game with checkout
    // validate if score is possible with the given amount of darts
    public void check(int darts, User u){
        Player nextPlayer = null;
        if (waitingForCheck == getPlayerByUser(u)){
            //currently waiting for checkout by this user
            nextPlayer = getPlayerByUser(u);
        } else if (waitingForCheck != null){
            //currently waiting for checkout by another user
            return;
        }
        //player is the regular next player
        if (nextPlayer == null){
            nextPlayer = players.get(intNextPlayer);
        }
        if (u.getId().equals(nextPlayer.getId())){
            int rem = nextPlayer.getCurrentScore();
            if (
                    (darts == 1 && rem < 41 && rem % 2 == 0) ||
                            (darts == 2 && (rem < 99 || Arrays.stream(new int[]{100, 101, 104, 107, 110}).anyMatch(notbogey -> notbogey == rem))) ||
                            (darts == 3 && (rem < 159 || Arrays.stream(new int[]{170, 167, 164, 161, 160}).anyMatch(notbogey -> notbogey == rem)))
            ) {
                nextPlayer.check(darts);
                MatchManager.getInstance().getMatchByChannel(channel).playerWonLeg(nextPlayer, darts);
            } else {
                EmbedBuilder eb = new EmbedBuilder().setColor(Color.red).setDescription("Your score of " + rem
                        + " cannot be finished with " + darts);
                if (darts == 1) {
                    eb.appendDescription(" dart.");
                } else {
                    eb.appendDescription(" darts.");
                }
                channel.sendMessage(eb.build()).queue();
            }
        }else {
            channel.sendMessage(
                    new EmbedBuilder().setDescription("It's <@" + nextPlayer.getId() + ">'s turn to throw. Please wait.").setColor(Color.red).build()
            ).queue();
        }
    }

    //corrects the players score.
    //Corrected Score is not validated mathematically
    public void correction(int points, User u) {
        if( points > 180 || points < 0 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible -> impossible == points) ){
            channel.sendMessage(
                    new EmbedBuilder().setDescription("This score cannot be achieved with three darts. Please submit the correct value.").setColor(Color.red).build()
            ).queue();
        } else {
            Player p = getPlayerByUser(u);
            if(p != null) {
                int newScore = p.correctLast(points);
                validateScore(p, newScore, false);
            } else {
                channel.sendMessage(
                        new EmbedBuilder().setDescription("Score could not be updated. User is not a player of this match.").setColor(Color.red).build()
                ).queue();
            }
        }
    }

    /*
     * prompts user to use check command if remaining would be set to 0
     * validates score to avoid impossible values
     */
    private void validateScore(Player nextPlayer, int newScore, boolean switchPlayer){
        waitingForCheck = null;
        if (newScore == 0){
            channel.sendMessage("How many darts did you use to check out?").queue();
            waitingForCheck = nextPlayer;
        } else if (newScore < 0 || newScore == 1) {
            MessageAction msg = channel.sendMessage("Busted! <@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(nextPlayer.getCurrentScore())).append(" left.");
            if(switchPlayer) {
                determineNextPlayer();
            }
            nextPlayer = players.get(intNextPlayer);
            msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(nextPlayer.getCurrentScore())).append(".").queue();
        } else {
            MessageAction msg = channel.sendMessage("<@").append(nextPlayer.getId()).append("> has ").append(String.valueOf(nextPlayer.getCurrentScore())).append(" left.");
            if(switchPlayer) {
                determineNextPlayer();
            }
            nextPlayer = players.get(intNextPlayer);
            msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">\nYou require ").append(String.valueOf(nextPlayer.getCurrentScore())).append(".").queue();
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
        if (++intNextPlayer >= this.players.size()){
            intNextPlayer = 0;
        }
    }

    public List<Player> getPlayers() {
        return players;
    }
    public Player getWaitingForCheck() {
        return waitingForCheck;
    }
}

