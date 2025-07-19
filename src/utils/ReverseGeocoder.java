//this class is used for getting addres on marker in gooogle maps
package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class ReverseGeocoder {
public static double[] getCoordinates(String address) {
try {
String encoded = URLEncoder.encode(address, "UTF-8");
String urlStr = "https://nominatim.openstreetmap.org/search?q=" + encoded + "&format=json&limit=1";
URL url = new URL(urlStr);
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestProperty("User-Agent", "JavaFX-EMS");  // Required by Nominatim
conn.setRequestMethod("GET");

BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
StringBuilder response = new StringBuilder();
String line;
while ((line = in.readLine()) != null) {
    response.append(line);
}
in.close();

JSONArray arr = new JSONArray(response.toString());
if (arr.isEmpty()) return null;

JSONObject obj = arr.getJSONObject(0);
double lat = obj.getDouble("lat");
double lon = obj.getDouble("lon");

return new double[]{ lat, lon };
} catch (Exception e) {
e.printStackTrace();
return null;
}
}
}
