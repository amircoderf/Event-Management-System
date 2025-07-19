package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ReverseGeocodingService {public static String getOfficialAddress(String input) {
    try {
        String query = URLEncoder.encode(input, StandardCharsets.UTF_8);
        String url = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "EMS Student App");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        JsonArray arr = JsonParser.parseString(reader.readLine()).getAsJsonArray();
        if (!arr.isEmpty()) {
            JsonObject obj = arr.get(0).getAsJsonObject();
            return obj.get("display_name").getAsString(); // ✅ return only formatted address
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}