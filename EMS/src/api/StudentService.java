package api;

import com.google.gson.*;
import model.Event;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StudentService {
    private static final String BASE_URL = "http://localhost/ems-api/studentService/";
    private static final Gson gson = new Gson();

    public static List<Event> getMyEvents(int studentId) {
        List<Event> events = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "myEvents.php?student_id=" + studentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JsonArray array = JsonParser.parseString(response.toString()).getAsJsonArray();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                Event e = new Event(
                    obj.get("event_id").getAsInt(),
                    obj.get("title").getAsString(),
                    obj.get("description").getAsString(),
                    obj.get("datetime").getAsString(),
                    obj.get("location").getAsString(),
                    obj.get("capacity").getAsInt()
                );
                events.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public static List<Event> getPastEvents(int studentId) {
        List<Event> events = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "pastEvents.php?student_id=" + studentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            JsonArray array = JsonParser.parseString(response.toString()).getAsJsonArray();
            for (JsonElement el : array) {
                JsonObject obj = el.getAsJsonObject();
                Event e = new Event(
                    obj.get("event_id").getAsInt(),
                    obj.get("title").getAsString(),
                    obj.get("description").getAsString(),
                    obj.get("datetime").getAsString(),
                    obj.get("location").getAsString(),
                    obj.get("capacity").getAsInt()
                );
                events.add(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
    
    public static boolean registerForEvent(int studentId, int eventId) {
        try {
            URL url = new URL(BASE_URL + "registerEvents.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject obj = new JsonObject();
            obj.addProperty("student_id", studentId);
            obj.addProperty("event_id", eventId);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = in.readLine();
            return res != null && res.contains("successful");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
 
    public static boolean submitFeedback(int studentId, int eventId, int rating, String comment) {
        try {
            URL url = new URL(BASE_URL + "submitFeedback.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject obj = new JsonObject();
            obj.addProperty("student_id", studentId);
            obj.addProperty("event_id", eventId);
            obj.addProperty("rating", rating);
            obj.addProperty("comment", comment);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = in.readLine();
            return res != null && res.contains("submitted");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

} 
