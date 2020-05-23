package Managers;

import games.Match;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;
import java.util.Set;

/*
 *  Only one object is created from this class.
 *  The class maps text channels to their GameX01 objects
 */
public final class MatchManager {
    private static volatile MatchManager instance;

    private final HashMap<TextChannel, Match> games;

    private MatchManager() {
        this.games  = new HashMap<>();
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

    public int getNumberOfMatches(){
        return instance.games.size();
    }

    public void addMatch(TextChannel t, Match m){
        games.put(t,m);
        t.getJDA().getPresence().setActivity(Activity.playing(getNumberOfMatches() + " games of darts"));
    }

    public void removeMatchByChannel(TextChannel t){
        games.remove(t);

        t.getJDA().getPresence().setActivity(Activity.playing(getNumberOfMatches() + " games of darts"));
    }

    public Match getMatchByChannel(TextChannel t){
        return games.getOrDefault(t, null);
    }

    public Set<TextChannel> getUsedChannels() {
        return games.keySet();
    }
}
