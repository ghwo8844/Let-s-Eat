/**
 * Created by Hojae Jung on 2/3/2017.
 */
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.imageio.ImageIO;


class GooglePlacesAPIHandler {
    private double lat, lon;
    private CloseableHttpClient client;
    private String APIKey;

    public GooglePlacesAPIHandler() throws IOException {
        client = HttpClients.createDefault();
        BufferedReader getAPIKey = new BufferedReader(new FileReader(new File("GooglePlacesAPIKey.txt")));
        APIKey = getAPIKey.readLine().trim();
        getAPIKey.close();
        lat = Double.MAX_VALUE;
        lon = Double.MAX_VALUE;
    }

    public GooglePlacesAPIHandler(double lat, double lon) throws IOException {
        this();
        this.lat = lat;
        this.lon = lon;
    }

    public LinkedList<Place> getSearchResult(String query) {
        try {
            URI uri;
            if(this.lat < Double.MAX_VALUE) {
                uri = new URIBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json")
                        .addParameter("query", query)
                        .addParameter("latitude", Double.toString(lat))
                        .addParameter("longitude", Double.toString(lon))
                        .addParameter("radius", "30000")
                        .addParameter("key", APIKey)
                        .build();
            } else {
                uri = new URIBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json")
                        .addParameter("query", query)
                        .addParameter("radius", "30000")
                        .addParameter("key", APIKey)
                        .build();
            }
            JsonObject jo = getResponse(uri);
            JsonArray ja = jo.get("results").getAsJsonArray();
            LinkedList<Place> places = new LinkedList<>();
            for(JsonElement je : ja) {
                places.add(new Place(je.getAsJsonObject()));
            }
            return places;
        } catch(URISyntaxException e) {
            System.err.println("Google API disabled");
        } catch(IOException e) {
            System.err.println("No Response Error");
        }
        return null;
    }

    public HashMap<String, String> getPlaceDetail(Place place) {
        HashMap<String, String> result = new HashMap<>();
        try {
            URI uri = new URIBuilder("https://maps.googleapis.com/maps/api/place/details/json")
                    .addParameter("placeid", place.getId())
                    .addParameter("key", APIKey)
                    .build();
            JsonObject jo = getResponse(uri);
            if(jo.get("status").getAsString().equals("OK")) {
                jo = jo.get("result").getAsJsonObject();
                result.put("Address", jo.get("formatted_address").getAsString());
                result.put("Phone_Number", jo.get("formatted_phone_number").getAsString());
                result.put("Name", place.getName());
                result.put("Icon", place.getIcon());
                result.put("Open", Boolean.toString(place.isOpen()));
                result.put("Rating", jo.get("rating").getAsString());
                result.put("GoogleURL", jo.get("url").getAsString());
                return result;
            } else {
                System.err.println("Place Not Found");
                return null;
            }
        } catch(URISyntaxException e) {
            System.err.println("Google API disabled");
        } catch(IOException e) {
            System.err.println("No Response Error");
        }
        return null;
    }

    public List<BufferedImage> getImages(Place place, int height, int width) {
        List<BufferedImage> imgs = new ArrayList<>();
        try {
            for(JsonElement je : place.getPhoto()) {
                URI uri = new URIBuilder("https://maps.googleapis.com/maps/api/place/photo")
                        .addParameter("photoreference", je.getAsJsonObject().get("photo_reference").getAsString())
                        .addParameter("maxheight", ""+height)
                        .addParameter("maxwidth", ""+width)
                        .addParameter("key", APIKey)
                        .build();
                HttpGet httpGet = new HttpGet(uri);
                HttpResponse response = client.execute(httpGet);
                if(response.getStatusLine().getStatusCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    imgs.add(ImageIO.read(new File(br.readLine())));
                    br.close();
                } else {
                    System.err.println(response.getStatusLine() + ": Place Not Found");
                    return null;
                }
            }
            return imgs;
        } catch(URISyntaxException e) {
            System.err.println("Google API disabled");
        } catch(IOException e) {
            System.err.println("No Response Error");
        }
        return null;
    }

    private JsonObject getResponse(URI uri) throws IOException {
        HttpGet httpGet = new HttpGet(uri);
        HttpResponse response = client.execute(httpGet);
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(br.readLine()).getAsJsonObject();
        br.close();
        return jo;
    }
}
