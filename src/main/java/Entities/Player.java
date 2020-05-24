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
        //only save new score if it is valid
        if(newScore > 1){
            lastScore = currentScore;
            currentScore = newScore;
            legStats.put("Darts", legStats.get("Darts") + 3);
            legStats.put("Scored", legStats.get("Scored") + s);
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
        System.out.println(lastThrow);
        //only save new score if it is valid
        if(newScore > 1){
            currentScore = newScore;
            //recalculate score
            legStats.put("Scored", legStats.get("Scored") - lastThrow + s);
            //reset counters of last throw
            if(lastThrow == 180){ legStats.put("180", legStats.get("180") - 1); }
            if(lastThrow >= 140 && lastThrow <= 179){ legStats.put("140+", legStats.get("140+") - 1); }
            if(lastThrow >= 100 && lastThrow <= 140){ legStats.put("100+", legStats.get("100+") - 1); }
            if(lastThrow == legStats.get("Highest")){ legStats.put("Highest",lastHighest); }
            //count new high scores
            if(s == 180){ legStats.put("180", legStats.get("180") + 1); }
            if(s >= 140 && s <= 179){ legStats.put("140+", legStats.get("140+") + 1); }
            if(s >= 100 && s <= 140){ legStats.put("100+", legStats.get("100+") + 1); }
            if(s > legStats.get("Highest")){ legStats.put("Highest",s); }
        }
        return newScore;
    }

    public void check(int d){
        legStats.put("Darts", legStats.get("Darts") + d);
        legStats.put("Scored", legStats.get("Scored") + currentScore);
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
    }

    public void finishLeg(boolean isWinner){
        //add leg stats to match stats
        if(isWinner){
            if(legStats.get("Darts") < matchStats.get("Best Leg") || matchStats.get("Best Leg") == 0){
                matchStats.put("Best Leg", legStats.get("Darts"));
            }
        }
        matchStats.put("Darts", matchStats.get("Darts") + legStats.get("Darts"));
        matchStats.put("Scored", matchStats.get("Scored") + legStats.get("Scored"));
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
