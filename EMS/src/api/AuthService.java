package api;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthService {

    public static JSONObject login(String email, String password, String role) throws Exception {
        URL url = new URL("http://localhost/ems-api/login.php"); 
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        request.put("email", email);
        request.put("password", password);
        request.put("role", role);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(request.toString().getBytes());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseText = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseText.append(line);
        }

        JSONObject response = new JSONObject(responseText.toString());

        if (response.has("error")) {
            throw new Exception(response.getString("error"));
        }

        return response;
    }

    public static boolean register(String name, String email, String password, String role, String extra1, String extra2) throws Exception {
        String endpoint;
        JSONObject request = new JSONObject();
        request.put("name", name);
        request.put("email", email);
        request.put("password", password);

        if ("student".equals(role)) {
            endpoint = "http://localhost/ems-api/studentService/registerStudent.php";
            request.put("student_number", extra1);
            request.put("course", extra2);
        } else if ("organizer".equals(role)) {
            endpoint = "http://localhost/ems-api/organizerService/registerOrganizer.php";
            request.put("organization", extra1);
            request.put("phone", extra2);
        } else {
            throw new IllegalArgumentException("Invalid role");
        }

        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(request.toString().getBytes());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responseText = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseText.append(line);
        }

        String responseStr = responseText.toString().trim();

        // Log raw response for debugging
        System.out.println("Server response: " + responseStr);

        // Ensure it's a valid JSON
        if (!responseStr.startsWith("{")) {
            throw new Exception("Invalid response from server: " + responseStr);
        }

        JSONObject response = new JSONObject(responseStr);

        if (response.has("message") && response.getString("message").contains("registered")) {
            return true;
        } else if (response.has("error")) {
            throw new Exception(response.getString("error"));
        }

        return false;
    }

}
