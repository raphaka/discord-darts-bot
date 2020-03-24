import events.GameOnEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
    public static void main(String[] args) throws javax.security.auth.login.LoginException{
        System.out.println("hello darts bot");

        JDA jda = new JDABuilder(args[0]).build();

        jda.addEventListener(new GameOnEvent());
    }
}

