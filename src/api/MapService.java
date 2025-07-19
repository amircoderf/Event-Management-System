package api;

public class MapService {
    private static final String API_KEY = "AIzaSyBcXluf4TUf36EQBVlYCgb7wO580gBHOUs";  // Replace with your API key

    public static String getStaticMapUrl(String location) {
        try {
            String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
            return String.format(
                "https://maps.googleapis.com/maps/api/staticmap?center=%s&zoom=15&size=600x300&markers=color:red|%s&key=%s",
                encodedLocation, encodedLocation, API_KEY
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
