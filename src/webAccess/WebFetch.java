package webAccess;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class WebFetch {

    public static String fetch(String site) //returns HTML of a given site as plaintext
    {
        String content = null;
        URLConnection connection = null;
        try {
            connection =  new URL(site).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return content;
    } //some mystic voodoo code I found on stack overflow

    public static void main(String[] args) {
        System.out.println("\n\n\n" + StringTools.format(fetch("https://benchmarks.ul.com/")));
    }
}
