package raymond.systemspecbot.pcparts;


import com.google.gson.JsonObject;

public class UserSpecs {

    private final String userId;
    private Cpu userCpu;
    private Gpu userGpu;
    private int userRam;
    private boolean specsPrivacy;
    private String pcDescription;

    public UserSpecs(String id, Cpu c, Gpu g, int r) {
        userId = id;
        userCpu = c;
        userGpu = g;
        userRam = r;
        specsPrivacy = true;
    }

    public UserSpecs(String id, Cpu c, Gpu g, int r, boolean priv, String desc) {
        this(id, c, g, r);
        specsPrivacy = priv;
        pcDescription = desc;
    }

    public UserSpecs(String id, Object obj) {
        userId = id;
        if (obj instanceof Cpu) {
            userCpu = (Cpu) obj;
        } else if (obj instanceof Gpu) {
            userGpu = (Gpu) obj;
        } else if (obj instanceof Integer) {
            userRam = (int) obj;
            if (userRam < 2) {
                userRam = 2;
            }
        }
    }

    //for converting info from file back to ArrayList<UserSpecs>
    public UserSpecs(JsonObject json) {
        userId = json.get("userId").getAsString();
        userCpu = new Cpu(json.get("cpu").getAsString());
        userGpu = new Gpu(json.get("gpu").getAsString());
        userRam = json.get("ram").getAsInt();
        specsPrivacy = json.get("privacy").getAsBoolean();
        pcDescription = json.get("description").getAsString();
    }

    //"GETTER" METHODS
    public String getUserId() {
        return userId;
    }

    public Cpu getUserCpu() {
        return userCpu;
    }

    //"SETTER" METHODS
    public void setUserCpu(Cpu c) {
        userCpu = c;
    }

    public Gpu getUserGpu() {
        return userGpu;
    }

    public void setUserGpu(Gpu g) {
        userGpu = g;
    }

    public int getUserRam() {
        if (userRam < 2) {
            userRam = 2;
        }
        return userRam;
    }

    public void setUserRam(int ram) {
        if (ram < 2)
            ram = 2;
        userRam = ram;
    }

    public String getPcDescription() {
        return pcDescription;
    }

    public void setPcDescription(String str) {
        pcDescription = str.trim();
    }

    public boolean getPrivacy() {
        return specsPrivacy;
    }

    public void setPrivacy(boolean bool) {
        specsPrivacy = bool;
    }

    public Boolean[] isBetterThan(UserSpecs other) {
        //Order is CPU, GPU, RAM
        Boolean[] output = new Boolean[3];

        output[0] = userCpu.isBetterThan(other.getUserCpu());
        output[1] = userGpu.isBetterThan(other.getUserGpu());

        if(userRam != other.getUserRam()) {
            output[2] = userRam > other.getUserRam();
        }

        return output;
    }

    //toString looks like this:
    // {userId: "", description: "", cpu: {CPU}, gpu: {GPU}, ram: 0, privacy: true}
    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();

        json.addProperty("userId", userId);
        json.addProperty("cpu", userCpu.toString());
        json.addProperty("gpu", userGpu.toString());
        json.addProperty("ram", userRam);
        json.addProperty("privacy", specsPrivacy);
        json.addProperty("description", pcDescription);

        return json;
    }
}
