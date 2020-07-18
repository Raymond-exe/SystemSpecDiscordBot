package raymond.systemspecbot.discordbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import raymond.systemspecbot.records.FirebaseController;
import raymond.systemspecbot.records.Recordkeeper;

import javax.security.auth.login.LoginException;

public class DiscordBot {

    private static JDA jda;
    public static boolean debugPrintouts = true;
    //public static String prefix = "~";


    public static void main(String[] args) throws LoginException {
        EnvironmentManager.instantiate();

        String discordToken = EnvironmentManager.get("SPECBOT_DISCORD_TOKEN");
        jda = new JDABuilder(AccountType.BOT).setToken(discordToken).build();

        FirebaseController.connect();

        Commands cmd = new Commands();

        jda.addEventListener(cmd);
    }

    public static JDA getJda() {
        return jda;
    }

}
