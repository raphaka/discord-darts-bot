package Managers;

import games.GameX01;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

/*
 *  Only one object is created from this class.
 *  The class maps text channels to their GameX01 objects
 */
public final class GameManager {
    private static volatile GameManager instance;

    private HashMap<TextChannel, GameX01> games;

    private GameManager() {
        this.games  = new HashMap<TextChannel, GameX01>();
    }

    // Get the only instance of this class
    public static GameManager getInstance() {
        if (instance == null) {
            synchronized (GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public void addGameX01(TextChannel t, GameX01 g){
        games.put(t,g);
    }
}
