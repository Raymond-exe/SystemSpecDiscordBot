package raymond.systemspecbot.records;

import raymond.systemspecbot.discordbot.DiscordBot;

import java.util.Map;

public class EnvironmentManager {

    private static Map<String, String> envVars;

    public static void instantiate() {
        envVars = System.getenv();

        DiscordBot.debugPrintln("Retrieved the following variables: " + envVars, EnvironmentManager.class);
    }

    public static String get(String key) {
        return envVars.get(key);
    }

}
