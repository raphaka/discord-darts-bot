package Managers;

import games.Match;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

/*
 *  Only one object is created from this class.
 *  The class maps text channels to their GameX01 objects
 */
public final class MatchManager {
    private static volatile MatchManager instance;

    private HashMap<TextChannel, Match> games;

    private MatchManager() {
        this.games  = new HashMap<TextChannel, Match>();
    }

    // Get the only instance of this class
    public static MatchManager getInstance() {
        if (instance == null) {
            synchronized (MatchManager.class) {
                if (instance == null) {
                    instance = new MatchManager();
                }
            }
        }
        return instance;
    }

    public void addMatch(TextChannel t, Match m){
        games.put(t,m);
    }

    public void removeMatchByChannel(TextChannel t){
        games.remove(t);
    }

    public Match getMatchByChannel(TextChannel t){
        return games.getOrDefault(t, null);
    }
}
