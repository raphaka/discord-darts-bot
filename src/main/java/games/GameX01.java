package games;

import Managers.MatchManager;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.*;


public class GameX01 {

    private TextChannel channel;
    private HashMap<User, Integer> scores = new  HashMap<User, Integer>();
    private int intNextPlayer;

    /*
     * Constructor sets the channel to be used.
     * The category of the channel is Dartboard (will be created if not existing)
     * A new channel in this category will be created or an old one will be used
     */
    public GameX01(TextChannel t, List<User> players){ //todo param starter
        this.channel = t;
        // set startscore for all players
        for (User player : players) {
            this.scores.put(player, 501);
        }
        //todo mechanism for choosing who begins
        //beginning player will be chosen randomly
        Random R = new Random();
        intNextPlayer = R.nextInt(scores.keySet().toArray().length);
        User nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
        channel.sendMessage(nextPlayer.getName()).append(" to throw first.\nGame on.").queue();
    }

    /*
     * Substracts the scored points from the remaining
     * prompts user to use check command if remaining would be set to 0
     * validates score to avoid impossible values
     */
    public void score(int points, User u){
         // determine who throws next
         User nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
         if (u != nextPlayer){
             channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
         } else {
             int remaining = scores.get(nextPlayer);
             // Check if points is valid and substract from current score if not checkout or overthrown.
             if( points>180 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible-> impossible == points) ){
                 channel.sendMessage("This score cannot be achieved with three darts. Please submit the correct value.").queue();
             } else {
                 if (points == remaining) {
                     channel.sendMessage("Finish the game by typing \"check\" and the number of darts needed, e.g. \"check 2.\n " +
                             "You can also type \"c1\", \"c2\" or \"c3\" to save time.").queue();
                 } else if (points > remaining || points == remaining-1) {
                     MessageAction msg = channel.sendMessage("Busted! Remaining: ").append(String.valueOf(remaining));
                     determineNextPlayer();
                     nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
                     msg.append("Next player: <@").append(nextPlayer.getId()).append(">").queue();
                 } else {
                     scores.put(nextPlayer, scores.get(nextPlayer) - points);
                     MessageAction msg = channel.sendMessage("Remaining: ").append(scores.get(nextPlayer).toString());
                     determineNextPlayer();
                     nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
                     channel.sendMessage("Next player: <@").append(nextPlayer.getId()).append(">").queue();
                 }
             }
         }
    }

    /*
     * Basically the same as the scoring function
     */
    public void remaining(int rem, User u){
        User nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
        if (u != nextPlayer){
            channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
        } else {
            int points = scores.get(nextPlayer) - rem;
            if (points < 0 || points>180 || Arrays.stream(new int[]{163,166,169,172,173,175,176,178,179}).anyMatch(impossible-> impossible == points)){
                channel.sendMessage("Your score of ").append(String.valueOf(points)).append(" cannot be achieved with three darts. Please correct your input.").queue();
            } else if (rem == 1 || rem <0){
                MessageAction msg = channel.sendMessage("Busted! Remaining: ").append(String.valueOf(rem));
                determineNextPlayer();
                nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
                msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">").queue();
            } else if(rem == 0){
                channel.sendMessage("Finish the game by typing \"check\" and the number of darts needed, e.g. \"check 2.\n " +
                        "You can also type \"c1\", \"c2\" or \"c3\" to save time.").queue();
            } else {
                scores.put(nextPlayer, rem);
                MessageAction msg = channel.sendMessage("Remaining: ").append(String.valueOf(rem));
                determineNextPlayer();
                nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
                msg.append("\nNext player: <@").append(nextPlayer.getId()).append(">").queue();
            }
        }
    }

    // end game with checkout
    // validate if score is possible with the given amount of darts
    public void check(int darts, User u){
        User nextPlayer = (User)scores.keySet().toArray()[intNextPlayer];
        if (u != nextPlayer){
            channel.sendMessage("It's <@").append(nextPlayer.getId()).append(">'s turn to throw. Please wait.").queue();
        } else {
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
        }
    }


    //count from player 0 to last player, then start again
    private void determineNextPlayer(){
        if (++intNextPlayer >= this.scores.keySet().toArray().length){
            intNextPlayer = 0;
        }
    }

}

