package pcParts;

public class Cpu {

    private String name;
    private double freqInGHz;  //GHz
    private double turboClock; //GHz
    private double baseClock;  //MHz

    public Cpu(String n, double freq, double turbo, double base) {
        name = n;
        freqInGHz = freq;
        turboClock = turbo;
        baseClock = base;
    }

    public Cpu(String rawText) {

        if (rawText.contains("{") && rawText.contains("}")) {
            name = rawText.substring(rawText.indexOf("{name=") + 6, rawText.indexOf(", freqInGHz=")).trim();
            freqInGHz = Double.parseDouble(rawText.substring(rawText.indexOf("freqInGHz=") + 10, rawText.indexOf(", turboClock=")).trim());
            turboClock = Double.parseDouble(rawText.substring(rawText.indexOf("turboClock=") + 11, rawText.indexOf(", baseClock=")).trim());
            baseClock = Double.parseDouble(rawText.substring(rawText.indexOf("baseClock=") + 10, rawText.indexOf("}")).trim());
        } else {
            System.out.println("[DEBUG - Cpu] Unaccepted parameter: " + rawText);
        }

    }

    public String getName() { return name; }
    public double getFreqInGHz() { return freqInGHz; }
    public double getTurboClock() { return turboClock; }
    public double getBaseClock() { return baseClock; }

    public boolean isBetterThan(Cpu other) {
        int counter = 0;

        if (freqInGHz > other.getFreqInGHz())
            counter++;
        if (turboClock > other.getTurboClock())
            counter++;
        if(baseClock > other.getBaseClock())
            counter++;

        return counter >= 2;
    }

    public String toString() {
        return "{name=" + name
                + ", freqInGHz=" + freqInGHz
                + ", turboClock=" + turboClock
                + ", baseClock=" + baseClock
                + "}";
    }

}
