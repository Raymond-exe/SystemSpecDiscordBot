package raymond.systemspecbot.webaccess;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import raymond.systemspecbot.pcparts.Cpu;
import raymond.systemspecbot.pcparts.Gpu;

//represents CPU/GPUs as results of a search
public class SearchResult {

    private String name;
    private String link;
    //private double[] value;

    /*
    public SearchResult(String n, double dOne, double dTwo, double dThree) {
        name = n;

        value = new double[3];
        value[0] = dOne;
        value[1] = dTwo;
        value[2] = dThree;
    } //*/

    public SearchResult(String n, String l) {
        name = n;
        link = l;
    }

    /*/only accepts html from "<td>" to "</td>"
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
    } //*/

    public String getName() { return name; }
    public String getLink() { return link; }

    public Cpu getCpu() {
        int freqIndex = -1, turboIndex = -1, coresIndex = -1, threadsIndex = -1;
        double freq = -1, turbo = -1;
        int cores = -1, threads = -1;
        String[] tempArgs;


        try {
            Document doc = WebFetch.fetch(link);

            Elements titles = doc.getElementsByTag("th");
            Elements values = doc.getElementsByTag("td");

            //*
            for(int i = 0; i < titles.size(); i++) {
                switch (titles.get(i).text().trim()) {
                    case "Frequency:":
                        freqIndex = i;
                        break;
                    case "Turbo Clock:":
                        turboIndex = i;
                        break;
                    case "# of Cores:":
                        coresIndex = i;
                        break;
                    case "# of Threads:":
                        threadsIndex = i;
                        break;
                    default:
                        break;
                }
            } //*/

            //Assigning the frequency
            tempArgs = values.get(freqIndex).text().split(" ");
            for (int i = 0; i < tempArgs.length; i++) {
                try {
                    freq = Double.parseDouble(tempArgs[i].trim());
                    break;
                } catch (Exception e) {}
            }

            //Assigning the turbo clock
            tempArgs = values.get(turboIndex).text().split(" ");
            for (int i = 0; i < tempArgs.length; i++) {
                try {
                    turbo = Double.parseDouble(tempArgs[i].trim());
                    break;
                } catch (Exception e) {}
            }

            //Assigning the core count
            tempArgs = values.get(coresIndex).text().split(" ");
            for (int i = 0; i < tempArgs.length; i++) {
                try {
                    cores = Integer.parseInt(tempArgs[i].trim());
                    break;
                } catch (Exception e) {}
            }

            //Assigning the thread count
            tempArgs = values.get(threadsIndex).text().split(" ");
            for (int i = 0; i < tempArgs.length; i++) {
                try {
                    threads = Integer.parseInt(tempArgs[i].trim());
                    break;
                } catch (Exception e) {}
            }

            return new Cpu(name, freq, turbo, cores, threads);
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }
    public Gpu getGpu() {
        int baseIndex = -1, boostIndex = -1, memIndex = -1, dxIndex = -1;
        double base = -1, boost = -1, mem = -1;
        int dx = -1;
        String[] tempArgs;


        try {
            Document doc = WebFetch.fetch(link);

            Elements titles = doc.getElementsByTag("dt");
            Elements values = doc.getElementsByTag("dd");

            //*
            for(int i = 0; i < titles.size(); i++) {
                switch (titles.get(i).text().trim()) {
                    case "Base Clock":
                        baseIndex = i;
                        break;
                    case "Boost Clock":
                        boostIndex = i;
                        break;
                    case "Memory Clock":
                        memIndex = i;
                        break;
                    case "DirectX":
                        dxIndex = i;
                        break;
                    default:
                        break;
                }
            } //*/

            //Assigning the frequency
            if(baseIndex != -1) {
                tempArgs = values.get(baseIndex).text().split(" ");
                for (int i = 0; i < tempArgs.length; i++) {
                    try {
                        base = Double.parseDouble(tempArgs[i].trim());
                        break;
                    } catch (Exception e) {
                    }
                }
            }

            //Assigning the turbo clock
            if(boostIndex != -1) {
                tempArgs = values.get(boostIndex).text().split(" ");
                for (int i = 0; i < tempArgs.length; i++) {
                    try {
                        boost = Double.parseDouble(tempArgs[i].trim());
                        break;
                    } catch (Exception e) {
                    }
                }
            }

            //Assigning the core count
            if(memIndex != -1) {
                tempArgs = values.get(memIndex).text().split(" ");
                for (int i = 0; i < tempArgs.length; i++) {
                    try {
                        mem = Double.parseDouble(tempArgs[i].trim());
                        break;
                    } catch (Exception e) {
                    }
                }
            }

            //Assigning the DirectX index
            if(dxIndex != -1) {
                tempArgs = values.get(dxIndex).text().split(" ");
                for (int i = 0; i < tempArgs.length; i++) {
                    try {
                        dx = Integer.parseInt(tempArgs[i].trim());
                        break;
                    } catch (Exception e) {
                    }
                }
            }

            return new Gpu(name, base, boost, mem, dx);
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }

    public static void main(String[] args) {
        SearchResult result = new SearchResult("name", "https://www.techpowerup.com/cpu-specs/core-i7-10700k.c2215");

        System.out.println(result.getCpu());
    }

}
