package raymond.systemspecbot.webaccess;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import raymond.systemspecbot.discordbot.DiscordBot;

public class WebFetch {

    public static Document fetch(String site, boolean ignoreContentType) {
        DiscordBot.debugPrintln("GET request sent to " + site + "...", WebFetch.class);

        try {
            Document doc = Jsoup.connect(site).userAgent("Mozilla/5.0").ignoreContentType(ignoreContentType).get();
            DiscordBot.debugPrintln("Connection successful!", WebFetch.class);
            return doc;

        } catch (Exception e) {
            DiscordBot.debugPrintln("Connection failed.", WebFetch.class);
            e.printStackTrace();
        }

        return null;
    }

    public static Document fetch(String site) {
        return fetch(site, false);
    }

    public static String fetchString(String site) {
        return null;
    }

    public static void main(String[] args) {
        System.out.println("\n\n\n" + StringTools.format(fetch("https://www.pcgamebenchmark.com/warzone-system-requirements").outerHtml()));
    }
}
