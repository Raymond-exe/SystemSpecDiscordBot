package raymond.systemspecbot.webaccess;

import raymond.systemspecbot.pcparts.Cpu;
import raymond.systemspecbot.pcparts.Gpu;
import raymond.systemspecbot.pcparts.SearchResult;

import java.util.ArrayList;

public class Searcher {

    private static String gamesSearchURL = "https://gamesystemrequirements.com/"; //search?q=
    private static String specSearchURL = "https://duckduckgo.com/";
    private static String gpuSearch = "+site:techpowerup.com/gpu-specs/";
    private static String cpuSearch = "+site:techpowerup.com/cpu-specs/";

    //Used for searching for both GPUs and CPUs
    private static ArrayList<SearchResult> searchSpecs(String spec, String query) {
        ArrayList<SearchResult> output = new ArrayList<>();
        String html, searchModifier;
        if (spec.equalsIgnoreCase("cpu")) {
            searchModifier = cpuSearch;
        } else if (spec.equalsIgnoreCase("gpu")) {
            searchModifier = gpuSearch;
        } else {
            return null;
        }

        query = StringTools.cleanString(query.trim());

        //replaces all spaces with "+"
        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == ' ')
                query = query.substring(0, i) + "+" + query.substring(i + 1);
        }

        html = WebFetch.fetch(specSearchURL + query + searchModifier);

        if (html.contains("It looks like there aren't any great matches for your search</div>"))
            return new ArrayList<>();

        while (html.contains("https://www.techpowerup.com/")) {
            output.add(new SearchResult(html.substring(html.indexOf("https://www.techpowerup.com/"), html.indexOf("\"", html.indexOf("https://www.techpowerup.com/")))));

            html = html.substring(html.indexOf("https://www.techpowerup.com/") + 28);
        }


        return output;
    }

    //Used for searching for GPUs
    public static ArrayList<Gpu> searchGpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("GPU", query.toLowerCase());
        ArrayList<Gpu> output = new ArrayList<>();

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add((results.get(i).getGpu()));
        }

        return output;
    }

    //Used for searching for CPUs
    public static ArrayList<Cpu> searchCpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("CPU", query.toLowerCase());
        ArrayList<Cpu> output = new ArrayList<>();

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add((results.get(i).getCpu()));
        }

        return output;
    }

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

        html = WebFetch.fetch(gamesSearchURL + "search?q=" + query);
        if (html.indexOf("<td class='tbl5'>", html.indexOf("<td class='tbl5'>Game</td>")) == -1) {
            output.add(null);
            return output;
        }
        if (html.indexOf("<td class='tbl5'>", html.indexOf("<td class='tbl5'>Game</td>") + 6) != -1) {
            html = html.substring(html.indexOf("<td class='tbl5'>Game</td>") + 31, html.indexOf("<td class='tbl5'>", html.indexOf("<td class='tbl5'>Game</td>") + 6));
        } else if (html.contains("<td class='tbl5'>Game</td>") && html.indexOf("</tbody>", html.indexOf("<td class='tbl5'>Game</td>") + 6) != -1) {
            html = html.substring(html.indexOf("<td class='tbl5'>Game</td>") + 31, html.indexOf("</tbody>", html.indexOf("<td class='tbl5'>Game</td>") + 6));
        }
        //*

        String temp;

        while (html.contains("</tr>")) {
            temp = html.substring(html.indexOf("'>", html.indexOf("<a href='") + 9) + 2, html.indexOf("</a>")) + " ("
                    + gamesSearchURL + html.substring(html.indexOf("<a href='") + 9, html.indexOf("'>", html.indexOf("<a href='") + 9)) + ")";

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
        if (!foundExactMatch) {
            for (String str : output) {
                if (str.toLowerCase().contains(originalQuery.toLowerCase())) {
                    output.remove(str);
                    output.add(0, str);
                    break;
                }
            }
        }

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

        System.out.println(searchCpu("Intel Core i5", 10));
    }

}
