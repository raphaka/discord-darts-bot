package Entities;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class Player {
    private final User user;
    private int currentScore;
    private int lastScore = 0;
    private int lastHighest = 0; //save highest score in case the new one gets corrected
    private final HashMap<String, Integer> legStats = new HashMap<>();

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
        //TODO update stats
        return newScore;
    }

    public void check(int d){
        legStats.put("Darts", legStats.get("Darts") + d);
        legStats.put("Scored", legStats.get("Scored") + currentScore);
        //TODO update more stats
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

    public HashMap<String, Integer> getLegStats(){
        return legStats;
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
