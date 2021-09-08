package raymond.systemspecbot.records;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import raymond.systemspecbot.discordbot.DiscordBot;
import raymond.systemspecbot.pcparts.Cpu;
import raymond.systemspecbot.pcparts.Gpu;
import raymond.systemspecbot.pcparts.UserSpecs;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FirebaseController {


    private static Firestore db;


    public static void connect() {

        DiscordBot.debugPrintln("Connecting to Firestore Database...", FirebaseController.class);

        GoogleCredentials googleCredentials;

        //try block will try to connect to Firebase
        try {

            //try block will try to locate credentials
            try {

                googleCredentials = GoogleCredentials.fromStream(
                        new ByteArrayInputStream(
                                EnvironmentManager.get("SPECBOT_GOOGLE_CREDENTIALS")
                                        .getBytes(StandardCharsets.UTF_8)
                        ));

            } catch (Exception e) {
                DiscordBot.debugPrintln("Unable to locate Environment Variable \"SPECBOT_GOOGLE_CREDENTIALS\", using application default", FirebaseController.class);
                googleCredentials = GoogleCredentials.getApplicationDefault();
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(googleCredentials)
                    .setDatabaseUrl("https://discord-specbot.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();

            DiscordBot.debugPrintln("Successfully connected to Firestore Database!", FirebaseController.class);
        } catch (Exception e) {
            DiscordBot.debugPrintln("Failed to connect to Firestore Database.", FirebaseController.class);
            e.printStackTrace();
        }
    }

    public static String getGuildPrefix(String guildId) {
        //request the guildPrefix with the matching guildId
        DocumentReference docRef = db.collection("guildPrefixes").document(guildId.trim());

        DiscordBot.debugPrintln("READ: A request for the guild " + guildId + "'s prefix went out!", FirebaseController.class);

        try {
            //If the requested guildId exists, return the prefix
            if (docRef.get().get().exists()) {
                return (String) docRef.get().get().get("prefix");
            }
            //if it doesn't exist, create a new guildPrefix entry and assign it the default prefix (~)
            else {

                HashMap<String, String> newPrefix = new HashMap<>();
                newPrefix.put("prefix", "~");

                docRef.set(newPrefix);
                return "~";
            }
        } catch (Exception e) {
            return "null";
        }

    }

    public static void setGuildPrefix(String guildId, String prefix) {
        HashMap<String, String> newPrefix = new HashMap<>();
        newPrefix.put("prefix", prefix);
        newPrefix.put("serverName", DiscordBot.getJda().getGuildById(guildId).getName());

        DiscordBot.debugPrintln(" WRITE: A request was sent to change the guildPrefix for guild " + guildId + " to " + prefix, FirebaseController.class);

        DocumentReference docRef = db.collection("guildPrefixes").document(guildId.trim());

        try {
            if (docRef.get().get().exists()) {
                docRef.set(newPrefix);
            }
        } catch (Exception ignored) {
        }
    }

    public static UserSpecs getUserSpecs(String userId) {
        DocumentReference docRef = db.collection("userSpecs").document(userId.trim());

        DiscordBot.debugPrintln("READ: A request went out for the UserSpecs of user <@!" + userId + ">", FirebaseController.class);

        try {
            //If user alrady has existing specs in database
            if (docRef.get().get().exists()) {
                Map<String, Object> specsMap = docRef.get().get().getData();
                String parseToUserSpecs = "<specs>";

                parseToUserSpecs += "<user>" + userId + "</user>";
                parseToUserSpecs += "<cpu>" + specsMap.get("cpu") + "</cpu>";
                parseToUserSpecs += "<gpu>" + specsMap.get("gpu") + "</gpu>";
                parseToUserSpecs += "<ram>" + specsMap.get("ram") + "</ram>";
                parseToUserSpecs += "<privacy>" + specsMap.get("privacy") + "</privacy>";
                parseToUserSpecs += "<description>" + specsMap.get("description") + "</description>";
                parseToUserSpecs += "</specs>";

                DiscordBot.debugPrintln("User <@!" + userId + ">'s UserSpecs were found!", FirebaseController.class);
                return new UserSpecs(parseToUserSpecs);
            } else {
                //UserSpecs for the userId weren't found
                DiscordBot.debugPrintln("No UserSpecs found for <@!" + userId + ">!", FirebaseController.class);

                HashMap<String, Object> newSpecs = Recordkeeper.getUserSpecsTemplate(userId);
                docRef.set(newSpecs);

                return getUserSpecs(userId); //TODO Fix! Bad Code >:(
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setUserSpecs(String userId, Object obj) {
        DocumentReference docRef = db.collection("userSpecs").document(userId.trim());
        String objClassAssignment;

        try {
            if (!docRef.get().get().exists()) {
                //UserSpecs for the userId didn't already exist
                DiscordBot.debugPrintln("No UserSpecs found for <@!" + userId + ">", FirebaseController.class);
                HashMap<String, Object> newSpecs = Recordkeeper.getUserSpecsTemplate(userId);
                docRef.set(newSpecs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (obj instanceof Integer) {
            objClassAssignment = "ram";

        } else if (obj instanceof Cpu) {
            objClassAssignment = "cpu";

        } else if (obj instanceof Gpu) {
            objClassAssignment = "gpu";

        } else if (obj instanceof String) {
            objClassAssignment = "description";

        } else if (obj instanceof Boolean) {
            objClassAssignment = "privacy";

        } else {
            DiscordBot.debugPrintln("setUserSpecs() was run with an unexpected object!", FirebaseController.class);
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put(objClassAssignment, obj);

        docRef.update(map);
    }

    public static boolean addUserSpecs(HashMap<String, Object> specsToAdd) {
        if (!specsToAdd.containsKey("userId"))
            return false;

        try {

            String userId = (String) specsToAdd.get("userId");

            db.collection("userSpecs").document(userId).set(specsToAdd);
            return specsToAdd.equals(db.collection("userSpecs").document(userId).get().get().getData());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        connect();

        setUserSpecs("defaultTemplate", 99);
    }

}
