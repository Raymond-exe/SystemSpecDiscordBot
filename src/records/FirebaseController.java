package records;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import discordBot.DiscordBot;
import pcParts.*;

public class FirebaseController {

    private static String serviceAccountFile = "C:\\Users\\Raymond\\Downloads\\specbot-serviceAccount.json";
    private static Firestore db;
    private static boolean debugPrintouts = true;

    //*
    public static void connect() {

        if (debugPrintouts)
            System.out.println("[DEBUG] Connecting to Firestore Database...");

        try {
            FileInputStream serviceAccount =
                    new FileInputStream(serviceAccountFile);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://discord-specbot.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            db = FirestoreClient.getFirestore();

            if (debugPrintouts)
                System.out.println("[DEBUG] Successfully connected to Firestore Database!");

        } catch (Exception e) {
            e.printStackTrace();

            if (debugPrintouts)
                System.out.println("[DEBUG] Failed to connect to Firestore Database");
        }
    }
    //*/

    public static String getGuildPrefix(String guildId) {
        //request the guildPrefix with the matching guildId
        DocumentReference docRef = db.collection("guildPrefixes").document(guildId.trim());

        if (debugPrintouts)
            System.out.println("[DEBUG] READ: A request for the guild prefix for " + guildId + " went out!");

        try{
            //If the requested guildId exists, return the prefix
            if (docRef.get().get().exists()) {
                return (String)docRef.get().get().get("prefix");
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

        if (debugPrintouts) {
            System.out.println("[DEBUG] WRITE: A request was sent to change the guildPrefix for guild " + guildId + " to " + prefix);
        }

        DocumentReference docRef = db.collection("guildPrefixes").document(guildId.trim());

        try {
            if (docRef.get().get().exists()) {
                docRef.set(newPrefix);
            }
        } catch (Exception e) {}
    }

    public static UserSpecs getUserSpecs(String userId) {
        DocumentReference docRef = db.collection("userSpecs").document(userId.trim());

        if (debugPrintouts) {
            System.out.println("[DEBUG] READ: A request went out for the UserSpecs of user #" + userId);
        }

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

                if (debugPrintouts) {
                    System.out.println("[DEBUG] User #" + userId + "'s UserSpecs were found!");
                    System.out.println("[DEBUG] " + specsMap);
                    System.out.println("[DEBUG] Output: " + parseToUserSpecs);
                }

                return new UserSpecs(parseToUserSpecs);

            } else {

                //UserSpecs for the userId weren't found
                if (debugPrintouts)
                    System.out.println("[DEBUG] No UserSpecs found for user #" + userId);

                HashMap<String, Object> newSpecs = Recordkeeper.getUserSpecsTemplate(userId);
                docRef.set(newSpecs);

                return getUserSpecs(userId); //TODO Fix! Bad Code >:(
            }
        } catch (Exception e) {
            if(debugPrintouts) {
                System.out.println("[DEBUG] An error has occured!");
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void setUserSpecs(String userId, Object obj) {
        DocumentReference docRef = db.collection("userSpecs").document(userId.trim());
        String objClassAssignment;

        try {
            if (!docRef.get().get().exists()) {
                //UserSpecs for the userId didn't already exist
                if (debugPrintouts)
                    System.out.println("[DEBUG] No UserSpecs found for user #" + userId);

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
            if (debugPrintouts) {
                System.out.println("[DEBUG] setUserSpecs was run with an unaccepted object!");
            }
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