import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Main {

    public static void main(String[] args) throws javax.security.auth.login.LoginException{
        JDA jda = JDABuilder.createDefault(args[0]).build();
        jda.addEventListener(new GameOnEvent());
        jda.addEventListener(new MatchEvent());
        jda.addEventListener(new RandomEvent());
        jda.addEventListener(new ScoreEvent());
        jda.addEventListener(new CheckEvent());
        jda.addEventListener(new QuitEvent());
        jda.addEventListener(new RemainingEvent());
        jda.addEventListener(new HelpEvent());
        jda.addEventListener(new CorrectEvent());
        jda.addEventListener(new StartScoreEvent());
        jda.getPresence().setActivity(Activity.playing("0 games of darts"));
    }

}

