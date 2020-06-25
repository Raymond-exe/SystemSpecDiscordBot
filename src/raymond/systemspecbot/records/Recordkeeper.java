package raymond.systemspecbot.records;

import raymond.systemspecbot.discordbot.DiscordBot;
import raymond.systemspecbot.pcparts.UserSpecs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;


public class Recordkeeper {

    private static HashMap<String, String> guildPrefixes = new HashMap<>();
    private static HashMap<String, UserSpecs> userSpecsMap = new HashMap<>();


    public static HashMap<String, Object> getUserSpecsTemplate(String userId) {
        HashMap<String, Object> output = new HashMap<>();

        output.put("userId", userId);
        output.put("cpu", "[No Cpu: 0]");
        output.put("gpu", "[No Gpu: 0]");
        output.put("ram", 2);
        output.put("description", "null");
        output.put("privacy", false);

        return output;
    }

    public static UserSpecs getSpecsByUserId(String userId) {

        if (userSpecsMap.containsKey(userId)) {
            return userSpecsMap.get(userId);
        } else {
            userSpecsMap.put(userId, FirebaseController.getUserSpecs(userId));
            return getSpecsByUserId(userId);
        }

        /*
        ArrayList<UserSpecs> UserSpecList = getUserSpecs();

        for (UserSpecs specs : UserSpecList) {
            if (specs.getUserId().equals(userId)) {
                return specs;
            }
        }

        addUserSpecs(new UserSpecs(userId, new Cpu("No CPU", 0), new Gpu("No GPU", 0), 0));

        return getSpecsByUserId(userId); //*/
    }

    public static boolean addUserSpecs(UserSpecs userSpecs) {
        HashMap<String, Object> parsedSpecs = new HashMap<>();

        parsedSpecs.put("userId", userSpecs.getUserId());
        parsedSpecs.put("cpu", userSpecs.getUserCpu());
        parsedSpecs.put("gpu", userSpecs.getUserGpu());
        parsedSpecs.put("ram", userSpecs.getUserRam());
        parsedSpecs.put("privacy", userSpecs.getPrivacy());
        parsedSpecs.put("description", userSpecs.getPcDescription());

        userSpecsMap.put(userSpecs.getUserId(), userSpecs);
        return FirebaseController.addUserSpecs(parsedSpecs);
        /*
        for (int i = 0; i < userSpecsList.size(); i++) {
            if (userSpecsList.get(i).getUserId().equals(userSpecs.getUserId()))
                userSpecsList.remove(i);
        } //*/
    }

    public static String getGuildPrefix(String guildId) {

        try {
            DiscordBot.getJda().getGuildById(guildId).getName();
        } catch (Exception e) {
            return "";
        }

        if (guildPrefixes.containsKey(guildId)) {
            return guildPrefixes.get(guildId);
        } else {
            String prefix = FirebaseController.getGuildPrefix(guildId);
            guildPrefixes.put(guildId, prefix);
            return getGuildPrefix(guildId);
        }
    }

    /*
    public static void saveGuildPrefixes() {
        writeToFile(guildPrefixesFile, guildPrefixesList);
    }
    public static void saveUserSpecs() { writeToFile(userSpecsFile, toStringArrayList(userSpecsList)); }

    /*
    public static boolean setPrefix(String guildId, String prefix) {

        for (int i = 0; i < guildPrefixes.size(); i++) {
            if (guildPrefixes.get(i).startsWith(guildId)) {
                guildPrefixes.set(i, guildId + "[" + prefix + "]");
            }
        }

        return getPrefixByGuildId(guildId).equals(prefix);
    } //*/

    public static void setPrefix(String guildId, String prefix) {
        guildPrefixes.put(guildId, prefix);
        FirebaseController.setGuildPrefix(guildId, prefix);
    }

    public static String readFile(String filename) {
        StringBuilder output = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String line = reader.readLine();
            while (line != null) {
                output.append(line).append("\n");
                line = reader.readLine();
            }

        } catch (Exception e) {
            System.out.println("Unable to find file \"" + filename + "\"");
        }

        return output.toString();
    }

    /*
    private static boolean writeToFile(String filename, ArrayList<String> contents) {
        String strToWrite = "";

        for (String str : contents) {
            strToWrite += str + "\n";
        }

        try {
            BufferedWriter writer = new BufferedWriter((new FileWriter(filename)));

            writer.write(strToWrite.toCharArray());
            writer.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static ArrayList<String> toStringArrayList(ArrayList<UserSpecs> array) {
        ArrayList<String> output = new ArrayList<>();

        for (UserSpecs user : array) {
            output.add(user.toString());
        }

        return output;
    }
    //*/
}
