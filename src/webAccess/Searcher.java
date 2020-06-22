package webAccess;

import pcParts.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Searcher {

    private static String gamesSearchURL = "https://gamesystemrequirements.com/"; //search?q=
    private static String specSearchURL = "https://benchmarks.ul.com/";
    private static String gpuSearch = "compare/best-gpus?search=";
    private static String cpuSearch = "compare/best-cpus?search=";

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

        for (int i = 0; i < query.length(); i++) {
            if (query.charAt(i) == ' ')
                query = query.substring(0, i) + "%20" + query.substring(i + 1);
        }

        html = WebFetch.fetch(specSearchURL + searchModifier + query);
        html = html.substring(html.indexOf("<tbody>"));

        while (html.contains("<tr>")) {
            output.add(new SearchResult(html.substring(html.indexOf("<tr>"), html.indexOf("</tr>"))));

            html = html.substring(html.indexOf("</tr>") + 5);
        }


        return output;
    }

    //Used for searching for GPUs
    public static ArrayList<Gpu> searchGpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("GPU", query.toLowerCase());
        ArrayList<Gpu> output = new ArrayList<>();
        int index = 0;

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add((results.get(i).getGpu()));

            if (output.get(i).getName().length() < results.get(index).getName().length()) {
                index = i;
            }
        }

        if (!output.isEmpty())
            output.add(0, output.remove(index));

        return output;
    }

    //Used for searching for CPUs
    public static ArrayList<Cpu> searchCpu(String query, int size) {
        ArrayList<SearchResult> results = searchSpecs("CPU", query.toLowerCase());
        ArrayList<Cpu> output = new ArrayList<>();
        int index = 0;

        for (int i = 0; i < results.size() && i < size; i++) {
            output.add(results.get(i).getCpu());

            if (output.get(i).getName().length() < results.get(index).getName().length()) {
                index = i;
            }
        }

        if (!output.isEmpty())
            output.add(0, output.remove(index));

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
        ArrayList<String> output = new ArrayList<String>();
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
        } else if (html.contains("<td class='tbl5'>Game</td>") && html.indexOf("</tbody>", html.indexOf("<td class='tbl5'>Game</td>") + 6) != -1){
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
            if(StringTools.cleanString(originalQuery.trim().toLowerCase()).equals(entryTitle)) {
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

        /*
        ArrayList<String> array = searchFor("Sword");
        System.out.println(array.toString());
        System.out.println("Number of Results: " + array.size());
        //*/
    }

}