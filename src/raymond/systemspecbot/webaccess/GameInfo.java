package raymond.systemspecbot.webaccess;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

    public HashMap getInfo() {
        return null;
    }

    public String getImageUrl() {
        return info().child(0).attr("src");
    }

    private Element content() {
        return doc.child(0).child(1).child(3).child(7);
    }

    private Element minimumRequirements() {
        return content().child(0).child(2).child(3);
    }

    private Element recommendedRequirements() {
        return content().child(0).child(2).child(1);
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

    public ArrayList<String> getMinimumSpecs() {
        return specs(minimumRequirements());
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
        final Element elem = requirements.child(1);
        final String query = elem.ownText();

        try {
            return Searcher.searchSpecs("GPU", query).get(0).getGpu();
        } catch (Exception e) {
            // no search results came up
            return new Gpu("Unknown", 0, 0, 0);
        }
    }

    public Cpu getRecommendedCpu() {
        return cpu(recommendedRequirements());
    }

    public Cpu getMinimumCpu() {
        return cpu(minimumRequirements());
    }

    private Cpu cpu(Element requirements) {
        final Element elem = requirements.child(2);
        final String query = elem.ownText();

        try {
            return Searcher.searchSpecs("CPU", query).get(0).getCpu();
        } catch (Exception e) {
            // no search results came up
            return new Cpu("Unknown", 0, 0, 0, 0);
        }
    }

    public static void main(String[] args) {
        GameInfo info = new GameInfo("https://www.pcgamebenchmark.com/valorant-system-requirements");
        System.out.println(info.getMinimumSpecs());
    }

}
