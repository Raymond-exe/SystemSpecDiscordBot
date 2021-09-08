package raymond.systemspecbot.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import raymond.systemspecbot.records.EnvironmentManager;
import raymond.systemspecbot.records.FirebaseController;

public class DiscordBot {

    private static JDA jda;
    private static final boolean debugPrintouts = true;


    public static void main(String[] args) {

        debugPrintln("Instantiating EnvironmentManager class...", DiscordBot.class);
        EnvironmentManager.instantiate();

        debugPrintln("Retrieving SPECBOT_CANARY_DISCORD_TOKEN...", DiscordBot.class);
        String discordToken = EnvironmentManager.get("SPECBOT_CANARY_DISCORD_TOKEN");

        try {
            debugPrintln("Logging in JDA...", DiscordBot.class);
            jda = JDABuilder.createDefault(discordToken).build();
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

    public static void debugPrintln(String debugMessage, Class source) {
        if(!debugPrintouts)
            return;

        System.out.println("[DEBUG - " + source.getSimpleName() + "] " + debugMessage);
    }

}
