package raymond.systemspecbot.webaccess;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import raymond.systemspecbot.discordbot.DiscordBot;

public class WebFetch {

    public static Document fetch(String site) {
        if (DiscordBot.debugPrintouts) { System.out.print("[DEBUG - WebFetch] Connection request sent to " + site + "..."); }

        try {
            Document doc = Jsoup.connect(site).userAgent("Mozilla/5.0").get();
            if (DiscordBot.debugPrintouts) { System.out.println(" Connection successful!"); }
            return doc;

        } catch (Exception e) {
            if (DiscordBot.debugPrintouts) { System.out.println(" Connection failed."); }
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        System.out.println("\n\n\n" + StringTools.format(fetch("https://duckduckgo.com/hello%20World").outerHtml()));
    }
}
