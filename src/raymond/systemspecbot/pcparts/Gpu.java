package raymond.systemspecbot.pcparts;

import com.google.gson.JsonObject;
import raymond.systemspecbot.webaccess.StringTools;

import java.util.HashMap;

public class Gpu {

    private String name;
    private double baseClock;  //MHz
    private double boostClock; //MHz
    private double memClock;   //MHz
    private int dxVersion;

    public static Gpu getGpuDefault() {
        return new Gpu("No GPU", 0, 0, 0);
    }

    public Gpu(String n, double base, double boost, double mem) {
        name = n;
        baseClock = base;
        boostClock = boost;
        memClock = mem;
        dxVersion = 9;
    }

    public Gpu(String n, double base, double boost, double mem, int dx) {
        name = n;
        baseClock = base;
        boostClock = boost;
        memClock = mem;

        if (dx >= 9 && dx <= 12)
            dxVersion = dx;
        else
            dxVersion = 9;

    }

    public Gpu(String rawString) {
        rawString = rawString.substring(1, rawString.length()-1);
        HashMap<String, String> propertiesMap = new HashMap<>();
        for(String str : rawString.split(",")) {
            propertiesMap.put(str.substring(0, str.indexOf("=")).trim(), str.substring(str.indexOf("=")+1).trim());
        }

        name = propertiesMap.get("name");
        baseClock = Double.parseDouble(propertiesMap.get("baseClock"));
        boostClock = Double.parseDouble(propertiesMap.get("boostClock"));
        memClock = Double.parseDouble(propertiesMap.get("memClock"));
        dxVersion = Integer.parseInt(propertiesMap.get("dxVersion"));
    }

    public String getName() {
        return name;
    }

    public double getBaseClock() {
        return baseClock;
    }

    public double getBoostClock() {
        return boostClock;
    }

    public double getMemClock() {
        return memClock;
    }

    public int getDxVersion() {
        return  dxVersion;
    }


    public boolean isBetterThan(Gpu other) {
        int counter = 0;

        if (baseClock >= other.getBaseClock())
            counter++;
        if (boostClock >= other.getBoostClock())
            counter++;
        if(memClock >= other.getMemClock())
            counter++;

        return counter >= 2;
    }

    public String toString() {
        return StringTools.jsonToString(toJson());
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("name", name);
        json.addProperty("baseClock", baseClock);
        json.addProperty("boostClock", boostClock);
        json.addProperty("memClock", memClock);
        json.addProperty("dxVersion", dxVersion);

        return json;
    }
}
