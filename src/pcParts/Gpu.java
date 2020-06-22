package pcParts;

public class Gpu {

    private String name;
    private int rank;

    public Gpu(String n, int r) {
        name = n;
        rank = r;
    }

    public Gpu(String rawText) {

        if(rawText.contains("[") && rawText.contains("]")) {
            name = rawText.substring(rawText.indexOf('[') + 1, rawText.indexOf(':')).trim();
            rank = Integer.parseInt(rawText.substring(rawText.indexOf(':') + 1, rawText.indexOf(']')).trim());
        } else if (rawText.contains("{") && rawText.contains("}")) {
            name = rawText.substring(rawText.indexOf("{name=") + 6, rawText.indexOf(", rank=")).trim();
            rank = Integer.parseInt(rawText.substring(rawText.indexOf(", rank=") + 7, rawText.indexOf('}')).trim());
        } else {
            name = rawText.substring(0, rawText.indexOf(":")).trim();
            rank = Integer.parseInt(rawText.substring(rawText.indexOf(":") + 1).trim());
        }
    }

    public String getName() { return name; }
    public int getRank() { return rank; }

    public boolean isBetterThan(Gpu other) {
        return (rank > other.getRank());
    }

    public boolean isWorseThan(Gpu other) { return !isBetterThan(other); }

    public String toString() {
        return "[" + name + ": " + rank + "] ";
    }
}
