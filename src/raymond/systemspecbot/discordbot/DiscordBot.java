package raymond.systemspecbot.discordbot;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import raymond.systemspecbot.records.EnvironmentManager;
import raymond.systemspecbot.records.FirebaseController;

public class DiscordBot {

    private static JDA jda;
    public static boolean debugPrintouts = true;


    public static void main(String[] args) {

        if(debugPrintouts)
            System.out.println("[DEBUG - DiscordBot] Instantiating EnvironmentManager class...");
        EnvironmentManager.instantiate();

        if(debugPrintouts)
            System.out.println("[DEBUG - DiscordBot] Retrieving SPECBOT_DISCORD_TOKEN...");
        String discordToken = EnvironmentManager.get("SPECBOT_DISCORD_TOKEN");


        if(debugPrintouts)
            System.out.println("[DEBUG - DiscordBot] Logging in JDA...");
        try {
            jda = new JDABuilder(AccountType.BOT).setToken(discordToken).build();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        FirebaseController.connect();

        Commands cmd = new Commands();

        jda.addEventListener(cmd);
    }

    public static JDA getJda() {
        return jda;
    }

}
