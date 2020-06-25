package pcParts;

public class SearchResult {

    private String name;
    private double[] value;

    /*
    public SearchResult(String n, double dOne, double dTwo, double dThree) {
        name = n;

        value = new double[3];
        value[0] = dOne;
        value[1] = dTwo;
        value[2] = dThree;
    } //*/

    //only accepts html from "<td>" to "</td>"
    public SearchResult(String html) {

        value = new double[3];

        if(html.contains("<h2>Clock Speeds</h2>")) {
            html = html.substring(html.indexOf("<h2>Clock Speeds</h2>"), html.indexOf("</section>", html.indexOf("<h2>Clock Speeds</h2>")));
            value[0] = Double.parseDouble(html.substring(html.indexOf("<dd>", html.indexOf("<dt>Base Clock</dt>") + 4), html.indexOf("MHz")).trim());
            value[1] = Double.parseDouble(html.substring(html.indexOf("<dd>", html.indexOf("<dt>Boost Clock</dt>") + 4), html.indexOf("MHz")).trim());
            value[2] = Double.parseDouble(html.substring(html.indexOf("<dd>", html.indexOf("<dt>Memory Clock</dt>") + 4), html.indexOf("MHz")).trim());
        } else if (html.contains("<h1>Performance</h1>")) {
            html = html.substring(html.indexOf("<h1>Performance</h1>"), html.indexOf("</section>", html.indexOf("<h1>Performance</h1>")));
            value[0] = Double.parseDouble(html.substring(html.indexOf("<td>", html.indexOf("<th>Frequency:</th>")), html.indexOf("GHz", html.indexOf("<th>Frequency:</th>"))).trim());
            value[0] = Double.parseDouble(html.substring(html.indexOf("<td>", html.indexOf("<th>Turbo Clock:</th>")), html.indexOf("GHz", html.indexOf("<th>Turbo Clock:</th>"))).trim());
            value[0] = Double.parseDouble(html.substring(html.indexOf("<td>", html.indexOf("<th>Base Clock:</th>")), html.indexOf("MHz", html.indexOf("<th>Base Clock:</th>"))).trim());
        }
    }

    public String getName() { return name; }

    public Cpu getCpu() { return new Cpu(name, value[0], value[1], value[2]); }
    public Gpu getGpu() { return new Gpu(name, value[0], value[1], value[2]); }

}
