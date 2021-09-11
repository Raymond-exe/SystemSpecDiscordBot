package raymond.systemspecbot.pcparts;

import com.google.gson.JsonObject;
import raymond.systemspecbot.webaccess.StringTools;

import java.util.HashMap;

public class Cpu {

    private String name;
    private double freqInGHz;  //GHz
    private double turboClock; //GHz
    private int coreCount;
    private int threadCount;

    public static Cpu getCpuDefault() {
        return new Cpu("No CPU", 0, 0, 0, 0);
    }

    public Cpu(String n, double freq, double turbo, int cores, int threads) {
        name = n;
        freqInGHz = freq;
        turboClock = turbo;
        coreCount = cores;
        threadCount = threads;
    }

    public Cpu(String rawString) {
        rawString = rawString.substring(1, rawString.length()-1);
        HashMap<String, String> propertiesMap = new HashMap<>();
        for(String str : rawString.split(",")) {
            propertiesMap.put(str.substring(0, str.indexOf("=")).trim(), str.substring(str.indexOf("=")+1).trim());
        }

        name = propertiesMap.get("name");
        freqInGHz = Double.parseDouble(propertiesMap.get("freqInGHz"));
        turboClock = Double.parseDouble(propertiesMap.get("turboClock"));
        coreCount = Integer.parseInt(propertiesMap.get("coreCount"));
        threadCount = Integer.parseInt(propertiesMap.get("threadCount"));
    }

    public String getName() { return name; }
    public double getFreqInGHz() { return freqInGHz; }
    public double getTurboClock() { return turboClock; }
    public int getCoreCount() { return coreCount; }
    public int getThreadCount() { return threadCount; }

    public boolean isBetterThan(Cpu other) {
        int counter = 0;

        if (freqInGHz >= other.getFreqInGHz())
            counter++;
        if (turboClock >= other.getTurboClock())
            counter++;
        if(coreCount >= other.getCoreCount())
            counter++;
        if(threadCount >= other.getThreadCount())
            counter++;

        return counter >= 3;
    }

    public String toString() {
        return StringTools.jsonToString(toJson());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("freqInGHz", freqInGHz);
        json.addProperty("turboClock", turboClock);
        json.addProperty("coreCount", coreCount);
        json.addProperty("threadCount", threadCount);

        return json;
    }

}
