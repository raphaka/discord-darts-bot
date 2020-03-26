import events.GameOnEvent;
import events.ScoreEvent;
import games.GameX01;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.HashMap;

public class Main {

    private static JDA jda;

    public static void main(String[] args) throws javax.security.auth.login.LoginException{
        jda = new JDABuilder(args[0]).build();
        jda.addEventListener(new GameOnEvent());
        jda.addEventListener(new ScoreEvent());
    }

    public static JDA getJda(){
        return jda;
    }
}

