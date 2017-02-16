import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by Hojae Jung on 2/11/2017.
 */
public class Place {
    private String id;
    private String name;
    private boolean open;
    private String icon;
    private JsonArray photo;

    public Place(JsonObject jo) {
        this.id = jo.get("id").getAsString();
        this.name = jo.get("name").getAsString();
        this.open = jo.get("opening_hours").getAsJsonObject().get("open_now").getAsBoolean();
        this.icon = jo.get("icon").getAsString();
        this.photo = jo.get("photos").getAsJsonArray();
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOpen() {
        return this.open;
    }

    public String getIcon() {
        return this.icon;
    }

    protected JsonArray getPhoto() {
        return this.photo;
    }
}
