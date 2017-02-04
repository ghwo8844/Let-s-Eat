/**
 * Created by Hojae Jung on 2/3/2017.
 */
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class ZomatoAPI {
    public static void main(String[] args) throws IOException, URISyntaxException {
        CloseableHttpClient client = HttpClients.createDefault();
        BufferedReader getAPIKey = new BufferedReader(new FileReader(new File("ZomatoAPIKey.txt")));
        URI uri = new URIBuilder("https://developers.zomato.com/api/v2.1/categories")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.addHeader("user-key", getAPIKey.readLine().trim());

        getAPIKey.close();

        HttpResponse response = client.execute(httpGet);

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(rd.readLine());

        if (element.isJsonObject()) {
            JsonObject albums = element.getAsJsonObject();
            JsonArray datasets = albums.getAsJsonArray("categories");
            for (int i = 0; i < datasets.size(); i++) {
                JsonObject dataset = datasets.get(i).getAsJsonObject();
                System.out.println(dataset.get("categories").getAsJsonObject().get("name").getAsString());
            }
        }

        rd.close();
    }
}
