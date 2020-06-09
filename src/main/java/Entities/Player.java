package Entities;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class Player {
    private final User user;
    private int currentScore;
    private int lastScore = 0;
    private int lastHighest = 0; //save highest score in case the new one gets corrected
    private final HashMap<String, Integer> legStats = new HashMap<>();
    private final HashMap<String, Integer> matchStats = new HashMap<>();

    public Player(User u){
        user = u;
    }

    //returns the result of the throw, it has to be validated in the calling function
    public int score(int s){
        int newScore = currentScore - s;
        lastScore = currentScore;
        if (newScore!=0){
            legStats.put("Darts", legStats.get("Darts") + 3);
        }
        //only save new score if it is valid
        if(newScore > 1){
            currentScore = newScore;
            legStats.put("Scored", legStats.get("Scored") + s);
            if(legStats.get("Darts") <= 9){
                legStats.put("First 9 Scored", legStats.get("Scored"));
                legStats.put("First 9 Darts", legStats.get("Darts"));
            }
            if(s == 180){ legStats.put("180", legStats.get("180") + 1); }
            if(s >= 140 && s <= 179){ legStats.put("140+", legStats.get("140+") + 1); }
            if(s >= 100 && s <= 140){ legStats.put("100+", legStats.get("100+") + 1); }
            if(s > legStats.get("Highest")){
                lastHighest = legStats.get("Highest");
                legStats.put("Highest",s);
            }
        }
        return newScore;
    }

    public int correctLast(int s){
        int newScore =  lastScore - s;
        int lastThrow = lastScore - currentScore;
        //only save new score if it is valid
        if(newScore > 1 || newScore == 0){
            if (newScore > 1) {
                currentScore = newScore;
                //recalculate score
                legStats.put("Scored", legStats.get("Scored") - lastThrow + s);
                if(legStats.get("Darts") <= 9){
                    legStats.put("First 9 Scored", legStats.get("Scored"));
                    legStats.put("First 9 Darts", legStats.get("Darts"));
                }
                //count new high scores
                if(s == 180){ legStats.put("180", legStats.get("180") + 1); }
                if(s >= 140 && s <= 179){ legStats.put("140+", legStats.get("140+") + 1); }
                if(s >= 100 && s <= 140){ legStats.put("100+", legStats.get("100+") + 1); }
                if(s > legStats.get("Highest")){ legStats.put("Highest",s); }
            } else {
                //check(int,User) validates the shot by using the score before the checkout
                currentScore = lastScore;
                //reset number of thrown darts from last throw when correcting to checkout
                legStats.put("Darts", legStats.get("Darts") - 3);
                //reset score to last throw
                legStats.put("Scored", legStats.get("Scored") - lastThrow);
            }
            //reset counters of last throw
            if(lastThrow == 180){ legStats.put("180", legStats.get("180") - 1); }
            if(lastThrow >= 140 && lastThrow <= 179){ legStats.put("140+", legStats.get("140+") - 1); }
            if(lastThrow >= 100 && lastThrow <= 140){ legStats.put("100+", legStats.get("100+") - 1); }
            if(lastThrow == legStats.get("Highest")){ legStats.put("Highest",lastHighest); }
        }
        return newScore;
    }

    public void check(int d){
        legStats.put("Darts", legStats.get("Darts") + d);
        legStats.put("Scored", legStats.get("Scored") + currentScore);
        if(legStats.get("Darts") == 9){
            legStats.put("First 9 Scored", legStats.get("Scored"));
            legStats.put("First 9 Darts", 9);
        }
        if(currentScore >= 140 && currentScore <= 170){ legStats.put("140+", legStats.get("140+") + 1); }
        if(currentScore >= 100 && currentScore <= 140){ legStats.put("100+", legStats.get("100+") + 1); }
        if(currentScore > legStats.get("Highest")){ legStats.put("Highest",currentScore); }
    }

    public void initLeg(int start, int last){
        currentScore = start;
        lastScore = last;
        legStats.put("Darts", 0);
        legStats.put("Scored", 0);
        legStats.put("Highest",0);
        legStats.put("100+", 0);
        legStats.put("140+", 0);
        legStats.put("180", 0);
        legStats.put("First 9 Scored", 0);
        legStats.put("First 9 Darts", 0); //Counts if a player throws 9 darts or 6 because opponent scores 9darter
    }

    public void finishLeg(boolean isWinner){
        //add leg stats to match stats
        if(isWinner){
            if(legStats.get("Darts") < matchStats.get("Best Leg") || matchStats.get("Best Leg") == 0){
                matchStats.put("Best Leg", legStats.get("Darts"));
            }
        }
        if(legStats.get("Darts") > matchStats.get("Worst Leg")){
            matchStats.put("Worst Leg", legStats.get("Darts"));
        }
        matchStats.put("Darts", matchStats.get("Darts") + legStats.get("Darts"));
        matchStats.put("Scored", matchStats.get("Scored") + legStats.get("Scored"));
        matchStats.put("First 9 Scored", matchStats.get("First 9 Scored") + legStats.get("First 9 Scored"));
        matchStats.put("First 9 Darts", matchStats.get("First 9 Darts") + legStats.get("First 9 Darts"));
        if(legStats.get("Highest") > matchStats.get("Highest")){
            matchStats.put("Highest", legStats.get("Highest"));
        }
        matchStats.put("100+", matchStats.get("100+") + legStats.get("100+"));
        matchStats.put("140+", matchStats.get("140+") + legStats.get("140+"));
        matchStats.put("180", matchStats.get("180") + legStats.get("180"));
    }

    public void initMatch(){
        matchStats.put("Darts", 0);
        matchStats.put("Scored", 0);
        matchStats.put("Highest",0);
        matchStats.put("100+", 0);
        matchStats.put("140+", 0);
        matchStats.put("180", 0);
        matchStats.put("Best Leg",0);
        matchStats.put("Worst Leg", 0);
        matchStats.put("First 9 Scored", 0);
        matchStats.put("First 9 Darts", 0);
    }

    public HashMap<String, Integer> getLegStats(){
        return legStats;
    }

    public HashMap<String, Integer> getMatchStats(){
        return matchStats;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    //return value from JDA object
    public String getId(){
        return user.getId();
    }

    //return value from JDA object
    public String getName(){
        return user.getName();
    }
}
