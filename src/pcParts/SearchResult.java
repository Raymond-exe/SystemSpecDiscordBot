package pcParts;

public class SearchResult {

    private String name;
    private int rank;
    private double popularity;

    public SearchResult(String n, int r, double p) {
        name = n;
        rank = r;
        popularity = p;
    }

    //only accepts html from "<td>" to "</td>"
    public SearchResult(String html) {
        //assigning name
        html = html.substring(html.indexOf("href"));
        name = html.substring((html.indexOf(">") + 1), html.indexOf("</a>"));

        //assigning rank
        html = html.substring(html.indexOf("bar-score"));
        rank = Integer.parseInt(html.substring(html.indexOf(">") + 1, html.indexOf("<")).trim());

        //*
        //assigning popularity
        html = html.substring(html.lastIndexOf("bar-score"));
        popularity = Double.parseDouble(html.substring(html.indexOf(">") + 1, html.indexOf("<")));
        //*/
    }

    public String getName() { return name; }
    public int getRank() { return rank; }
    public double getPopularity() { return popularity; }

    public Cpu getCpu() { return new Cpu(name, rank); }
    public Gpu getGpu() { return new Gpu(name, rank); }

}
