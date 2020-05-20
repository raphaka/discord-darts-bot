package Entities;

import net.dv8tion.jda.api.entities.User;

public class Player {
    private final User user;
    private int currentScore;
    private int lastScore;

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

    public void setCurrentScore(int s){
        currentScore = s;
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
