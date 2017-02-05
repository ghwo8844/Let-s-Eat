/**
 * Created by Hojae Jung on 2/3/2017.
 */
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


class GooglePlacesAPIHandler {
    private double lat, lon;
    private CloseableHttpClient client;
    private String APIKey;

    private class Place {
        

        public Place() {

        }
    }

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

    public JsonObject getSearchResult(String query) {
        try {
            URI uri = new URIBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json")
                    .addParameter("query", query)
                    .addParameter("latitude", Double.toString(lat))
                    .addParameter("longitude", Double.toString(lon))
                    .addParameter("radius", "30000")
                    .addParameter("key", APIKey)
                    .build();
            HttpGet httpGet = new HttpGet(uri);
            HttpResponse response = client.execute(httpGet);
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            JsonParser parser = new JsonParser();
            JsonObject jo = parser.parse(br.readLine()).getAsJsonObject();
            br.close();
            return jo;
        } catch(URISyntaxException e) {
            System.err.println("Google API disabled");
        } catch(IOException e) {
            System.err.println("No Response Error");
        }
        return null;
    }
}
