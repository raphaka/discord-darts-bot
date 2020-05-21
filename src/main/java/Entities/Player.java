package Entities;

import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;

public class Player {
    private final User user;
    private int currentScore;
    private int lastScore = 0;

    //TODO private HashMap<String, Integer> legStats = new HashMap<>();

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
        }
        return newScore;
    }
    public int correctLast(int s){
        int newScore =  lastScore - s;
        //only save new score if it is valid
        if(newScore > 1){
            currentScore = newScore;
        }
        return newScore;
    }

    public void setInitialScore(int start, int last){
        currentScore = start;
        lastScore = last;
    }

    public int getCurrentScore() {
        return currentScore;
    }

//    TODO public void resetLegStats(){
//
//    }

    //return value from JDA object
    public String getId(){
        return user.getId();
    }

    //return value from JDA object
    public String getName(){
        return user.getName();
    }
}
