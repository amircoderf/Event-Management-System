package api;

import com.google.gson.*;
import model.Event;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OrganizerService {
    private static final String BASE_URL = "http://localhost/ems-api/organizerService/";
    private static final Gson gson = new Gson();

    public static List<Event> getEventsByOrganizer(int organizerId) {
        List<Event> events = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "listEvents.php?organizer_id=" + organizerId);
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

    public static boolean createEvent(String title, String desc, String datetime, int capacity, String location, int organizerId) {
        try {
            URL url = new URL(BASE_URL + "createEvents.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject obj = new JsonObject();
            obj.addProperty("title", title);
            obj.addProperty("description", desc);
            obj.addProperty("datetime", datetime);
            obj.addProperty("capacity", capacity);
            obj.addProperty("location", location);
            obj.addProperty("organizer_id", organizerId);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = in.readLine();
            JsonObject response = JsonParser.parseString(res).getAsJsonObject();
            return response.has("success") && response.get("success").getAsBoolean();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean updateEvent(int eventId, String title, String desc, String datetime, int capacity, String location) {
        try {
            URL url = new URL(BASE_URL + "updateEvent.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject obj = new JsonObject();
            obj.addProperty("event_id", eventId);
            obj.addProperty("title", title);
            obj.addProperty("description", desc);
            obj.addProperty("datetime", datetime);
            obj.addProperty("capacity", capacity);
            obj.addProperty("location", location);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = in.readLine();
            return res != null && res.contains("updated");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteEvent(int eventId) {
        try {
            URL url = new URL(BASE_URL + "deleteEvent.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            JsonObject obj = new JsonObject();
            obj.addProperty("event_id", eventId);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes());
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = in.readLine();
            return res != null && res.contains("deleted");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<JsonObject> getFeedbackByEvent(int eventId) {
        List<JsonObject> feedbackList = new ArrayList<>();
        try {
            URL url = new URL(BASE_URL + "getFeedback.php?event_id=" + eventId);
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
                feedbackList.add(el.getAsJsonObject());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // 🟢 Get upcoming events (after now)
    public static List<Event> getUpcomingEventsByOrganizer(int organizerId) {
        List<Event> allEvents = getEventsByOrganizer(organizerId);
        List<Event> upcomingEvents = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Event e : allEvents) {
            try {
                LocalDateTime eventTime = LocalDateTime.parse(e.getDatetime(), formatter);
                if (eventTime.isAfter(now)) {
                    upcomingEvents.add(e);
                }
            } catch (Exception ex) {
                System.err.println("Date parse error: " + e.getDatetime());
            }
        }

        return upcomingEvents;
    }

    //Get past events (before now)
    public static List<Event> getPastEventsByOrganizer(int organizerId) {
        List<Event> allEvents = getEventsByOrganizer(organizerId);
        List<Event> pastEvents = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Event e : allEvents) {
            try {
                LocalDateTime eventTime = LocalDateTime.parse(e.getDatetime(), formatter);
                if (eventTime.isBefore(now)) {
                    pastEvents.add(e);
                }
            } catch (Exception ex) {
                System.err.println("Date parse error: " + e.getDatetime());
            }
        }

        return pastEvents;
    }
}
