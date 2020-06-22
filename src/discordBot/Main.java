package discordBot;

import records.FirebaseController;
import records.Recordkeeper;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Main {

    private static JDA jda;
    //public static String prefix = "~";


    public static void main(String[] args) throws LoginException {
        String discordToken = Recordkeeper.readFile("C:\\Users\\Raymond\\Documents\\discordBotToken-CanIPlay.txt").trim();
        jda = new JDABuilder(AccountType.BOT).setToken(discordToken).build();

        FirebaseController.connect();

        Commands cmd = new Commands();
        jda.addEventListener(cmd);
    }

    public static JDA getJda() { return jda; }

}
