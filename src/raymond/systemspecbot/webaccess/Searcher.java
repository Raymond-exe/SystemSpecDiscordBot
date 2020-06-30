package raymond.systemspecbot.webaccess;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Searcher {

    private static String gamesSearchURL = "https://gamesystemrequirements.com/"; //search?q=
    private static String specSearchURL = "https://www.google.com/search?&q=";
    private static String gpuSearch = "+%2Bsite%3Atechpowerup.com%2Fgpu-specs%2F";
    private static String cpuSearch = "+%2Bsite%3Atechpowerup.com%2Fcpu-specs%2F";

    //Used for searching for both GPUs and CPUs
    public static ArrayList<SearchResult> searchSpecs(String spec, String query) {
        ArrayList<SearchResult> output = new ArrayList<>();
        String searchModifier, attributeValue;
        Document doc;
        if (spec.equalsIgnoreCase("cpu")) {
            searchModifier = cpuSearch;
            attributeValue = "https://www.techpowerup.com/cpu-specs/";
        } else if (spec.equalsIgnoreCase("gpu")) {
            attributeValue = "https://www.techpowerup.com/gpu-specs/";
            searchModifier = gpuSearch;
        } else {
            return null;
        }

        query = StringTools.cleanString(query.trim());

        //replaces all spaces with "+"
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == ' ')
                query = query.substring(0, i) + "%20" + query.substring(i + 1);
        }

        doc = WebFetch.fetch(specSearchURL + query + searchModifier);

        if (doc.outerHtml().contains("It looks like there aren't any great matches for your search</div>")) {
            System.out.println("No results for " + query + " found.");
            return new ArrayList<>();
        }
        System.out.println("Search entries found!");

        Elements titleElements = doc.getElementsByTag("h3");
        Elements linkElements = doc.getElementsByAttributeValueContaining("href", attributeValue);

        String title, link;
        for (int i = 0; i < titleElements.size() && i < linkElements.size(); i++) {
            title = titleElements.get(i).text();

            try {
                title = title.substring(0, title.indexOf("Specs")).trim();
                link = linkElements.get(i).attr("href");
                link = link.substring(link.indexOf("https://"), link.indexOf("&", link.indexOf("https://")));
                output.add(new SearchResult(title, link));
            } catch (Exception e) {}
        }

        //searches for and removes duplicates
        for (int i = 0; i < output.size(); i++) {
            for (int j = i; j < output.size(); j++) {
                if (i != j && output.get(i).getLink().equals(output.get(j).getLink())) {
                    output.remove(j);
                    j--;
                }
            }
        }

        return output;
    }

    /*
    //Used for searching for GPUs
    public static ArrayList<Gpu> searchGpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("GPU", query.toLowerCase());
        ArrayList<Gpu> output = new ArrayList<>();

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add((results.get(i).getGpu()));
        }

        return output;
    } //*/

    /*
    //Used for searching for CPUs
    public static ArrayList<Cpu> searchCpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("CPU", query.toLowerCase());
        ArrayList<Cpu> output = new ArrayList<>();

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add((results.get(i).getCpu()));
        }

        return output;
    } //*/

    public static String getGameSiteLink(String query) {
        query = query.trim();

        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == ' ')
                query = query.substring(0, i) + "+" + query.substring(i + 1);
        }

        return gamesSearchURL + "search?q=" + query;
    }

    //Used for games search
    public static ArrayList<String> searchFor(String originalQuery) {
        ArrayList<String> output = new ArrayList<>();
        String html;
        String query = originalQuery.trim();

        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == ' ')
                query = query.substring(0, i) + "+" + query.substring(i + 1);
        }

        html = WebFetch.fetch(gamesSearchURL + "search?q=" + query).outerHtml();
        if (html.indexOf("<td class=\"tbl5\">", html.indexOf("<td class=\"tbl5\">Game</td>")) == -1) {
            output.add(null);
            return output;
        }
        if (html.indexOf("<td class=\"tbl5\">", html.indexOf("<td class=\"tbl5\">Game</td>") + 6) != -1) {
            html = html.substring(html.indexOf("<td class=\"tbl5\">Game</td>") + 31, html.indexOf("<td class=\"tbl5\">", html.indexOf("<td class=\"tbl5\">Game</td>") + 6));
        } else if (html.contains("<td class=\"tbl5\">Game</td>") && html.indexOf("</tbody>", html.indexOf("<td class=\"tbl5\">Game</td>") + 6) != -1) {
            html = html.substring(html.indexOf("<td class=\"tbl5\">Game</td>") + 31, html.indexOf("</tbody>", html.indexOf("<td class=\"tbl5\">Game</td>") + 6));
        }
        //*

        String temp;

        while (html.contains("</tr>")) {
            temp = html.substring(html.indexOf("\">", html.indexOf("<a href=\"") + 9) + 2, html.indexOf("</a>")) + " ("
                    + gamesSearchURL + html.substring(html.indexOf("<a href=\"") + 9, html.indexOf("\">", html.indexOf("<a href=\"") + 9)) + ")";

            output.add(temp);
            html = html.substring(html.indexOf("</tr>") + 5);
        } //*/

        for (int i = 0; i < output.size(); i++) {

            if (output.get(i).contains("&#")) {
                output.set(i, StringTools.fixString(output.get(i)));
            }
        }

        //before returning, try to find an exact match. if one is found, set it as the top result.
        String entryTitle;
        boolean foundExactMatch = false;
        for (int i = 0; i < output.size(); i++) {
            entryTitle = StringTools.cleanString(output.get(i).substring(0, output.get(i).indexOf("(")).trim()).toLowerCase();
            if (StringTools.cleanString(originalQuery.trim().toLowerCase()).equals(entryTitle)) {
                output.add(0, output.remove(i));
                foundExactMatch = true;
                break;
            }
        }


        //if not exact match is found, put whatever contains the query at the top
        /*
        if (!foundExactMatch) {
            for (String str : output) {
                if (str.toLowerCase().contains(originalQuery.toLowerCase())) {
                    output.remove(str);
                    output.add(0, str);
                    break;
                }
            }
        } //*/

        return output;
    }

    public static String getSearchResult(String query, int resultNum) {
        ArrayList<String> searchResults = searchFor(query);

        if (resultNum > searchResults.size())
            resultNum = searchResults.size();

        String requestedResult = searchResults.get(resultNum - 1);
        return requestedResult.substring(requestedResult.lastIndexOf("(") + 1, requestedResult.lastIndexOf(")"));
    }

    public static String getSearchResult(String query) {
        return getSearchResult(query, 1);
    }

    public static void main(String[] args) {
    }

}
