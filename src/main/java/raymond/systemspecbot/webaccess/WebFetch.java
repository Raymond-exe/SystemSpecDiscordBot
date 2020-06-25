package raymond.systemspecbot.webaccess;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;



public class WebFetch {

    private static boolean debugPrintouts = true;

    public static String fetch(String site) //returns HTML of a given site as plaintext
    {
        if (debugPrintouts) { System.out.print("[DEBUG - WebFetch] Connection request sent to " + site + "..."); }
        String content = null;
        URLConnection connection;
        try {
            connection = new URL(site).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
            if (debugPrintouts) { System.out.println(" Connection successful!"); }
        }catch ( Exception ex ) {
            if (debugPrintouts) { System.out.println(" Connection failed."); }
            ex.printStackTrace();
        }
        return content;
    } //some mystic voodoo code I found on stack overflow

    public static void main(String[] args) {
        System.out.println("\n\n\n" + StringTools.format(fetch("https://duckduckgo.com/hello%20World")));
    }
}
