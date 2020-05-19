package Entities;

import net.dv8tion.jda.api.entities.User;

public class Player {
    private final User user;
    private int currentScore;

    public Player(User u){
        user = u;
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
