package raymond.systemspecbot.webaccess;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import raymond.systemspecbot.discordbot.DiscordBot;
import raymond.systemspecbot.pcparts.Cpu;
import raymond.systemspecbot.pcparts.Gpu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameInfo {

    public static final int MIN_SYS_REQS = 0, REC_SYS_REQS = 1;
    private final String website;
    private final Document doc;

    public GameInfo(String site) {
        website = site;
        doc = WebFetch.fetch(site);
    }

    public String getWebsite() { return website; }

    public String html() { return doc.outerHtml(); }

    public String getTitle() {
        String title = content().child(0).child(0).text();
        return title.substring(0, title.indexOf(" System Requirements"));
    }

    public HashMap<String, String> getInfo() {
        HashMap<String, String> output = new HashMap<>();
        Elements elems = content().getElementsByClass("metabox");
        Element elem = null;
        for(Element possibleMatch : elems) {
            if(possibleMatch.hasAttr("id") && possibleMatch.attr("id").equals("details-meta")) {
                elem = possibleMatch;
                break;
            }
        }
        if(elem == null) {
            DiscordBot.debugPrintln("Could not find a matching element!", GameInfo.class);
            return null;
        }
        String text;
        for(Element entry : elem.child(1).child(0).children()) {
            text = entry.text();
            if(text.toLowerCase().startsWith("download")) {
                continue;
            }
            output.put(text.substring(0, text.indexOf(":")), text.substring(text.indexOf(":")+1).trim());
        }
        output.put("Description", elem.getElementsByClass("description").get(0).child(1).text());

        return output;
    }

    public String getImageUrl() {
        return info().child(0).attr("src");
    }

    private Element content() {
        return doc.child(0).child(1).child(3).child(7);
    }

    private Element minimumRequirements() {
        return content().getElementsByClass("requirements").get(0).child(1);
    }

    public boolean hasRecommendedRequirements() {
        return content().getElementsByClass("requirements").get(0).childrenSize() == 4;
    }

    private Element recommendedRequirements() {
        Element elem = content().getElementsByClass("requirements").get(0);
        if(elem.childrenSize() == 4) {
            return elem.child(3);
        }

        return null;
    }

    private Element info() {
        return content().child(1).child(1);
    }

    // FOR DEBUGGING ONLY
    private static void printChildren(Element elem) {
        for(int i = 0; i < elem.childrenSize(); i++) {
            System.out.println("\n\n" + i + ":\n" + elem.child(i));
        }
    }

    /**********SPECS**********/

    public ArrayList<String> getRecommendedSpecs() {
        return specs(recommendedRequirements());
    }

    public String getRecommendedSpecsFormatted() {
        return "**CPU:** " + cpuName(recommendedRequirements())
                + "\n**GPU:** " + gpuName(recommendedRequirements())
                + "\n**RAM:** " + getRecommendedRamInGb() + " GB";
    }

    public ArrayList<String> getMinimumSpecs() {
        return specs(minimumRequirements());
    }

    public String getMinimumSpecsFormatted() {
        return "**CPU:** " + cpuName(minimumRequirements())
                + "\n**GPU:** " + gpuName(minimumRequirements())
                + "\n**RAM:** " + getMinimumRamInGb() + " GB";
    }

    private ArrayList<String> specs(Element requirements) {
        return new ArrayList<String>(
                Arrays.asList(
                        "CPU: " + cpu(requirements).toString(),
                        "GPU: " + gpu(requirements).toString(),
                        "RAM: " + getRamInGb(requirements) + " GB"));
    }

    public int getRecommendedRamInGb() {
        return getRamInGb(recommendedRequirements());
    }

    public int getMinimumRamInGb() {
        return getRamInGb(minimumRequirements());
    }

    private int getRamInGb(Element requirements) {
        final Element elem = requirements.child(0);
        String[] possibleNumbers = elem.ownText().trim().split(" ");
        for(String str : possibleNumbers) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                // TODO idfk put something here
            }
        }
        return -1;
    }

    public Gpu getRecommendedGpu() {
        return gpu(recommendedRequirements());
    }

    public Gpu getMinimumGpu() {
        return gpu(minimumRequirements());
    }

    private Gpu gpu(Element requirements) {
        try {
            return Searcher.searchSpecs("GPU", gpuName(requirements)).get(0).getGpu();
        } catch (Exception e) {
            // no search results came up
            return new Gpu("Unknown", 0, 0, 0);
        }
    }

    private String gpuName(Element requirements) {
        return requirements.child(1).ownText();
    }

    public Cpu getRecommendedCpu() {
        return cpu(recommendedRequirements());
    }

    public Cpu getMinimumCpu() {
        return cpu(minimumRequirements());
    }

    private Cpu cpu(Element requirements) {
        try {
            return Searcher.searchSpecs("CPU", cpuName(requirements)).get(0).getCpu();
        } catch (Exception e) {
            // no search results came up
            return new Cpu("Unknown", 0, 0, 0, 0);
        }
    }

    private String cpuName(Element requirements) {
        return requirements.child(2).ownText();
    }

    public static void main(String[] args) {
        GameInfo info = new GameInfo("https://www.pcgamebenchmark.com/valorant-system-requirements");
        System.out.println(info.getMinimumSpecs());
    }

}
