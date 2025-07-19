package view;

import api.OrganizerService;
import api.ReverseGeocodingService; // Import ReverseGeocodingService
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Event;
import model.Organizer;

import java.awt.Desktop; // For opening map in browser
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.IntStream;

public class UpdateEventView extends VBox {

    private double selectedLat = 0; // Not used in this context, but keeping for consistency if needed later
    private double selectedLon = 0; // Not used in this context, but keeping for consistency if needed later
    private Label addressInfo = new Label("Formatted address will appear here"); // Not directly used, but for consistency

    public UpdateEventView(Stage stage, Organizer organizer, Event event) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label headerLabel = new Label("Update Event");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");


        TextField titleField = new TextField(event.getTitle());
        titleField.setPromptText("Title"); // Added prompt text for consistency

        TextArea descField = new TextArea(event.getDescription());
        descField.setPromptText("Description"); // Added prompt text for consistency

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date"); // Added prompt text for consistency

        // --- Time Pickers (ComboBoxes) - Adjusted to match CreateEventView ---
        ComboBox<Integer> hourBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) hourBox.getItems().add(i);
        hourBox.setPromptText("Hour");

        ComboBox<Integer> minuteBox = new ComboBox<>();
        for (int i = 0; i < 60; i += 5) minuteBox.getItems().add(i); // Increments of 5
        minuteBox.setPromptText("Minute");

        HBox timeBox = new HBox(10, hourBox, minuteBox);
        timeBox.setPadding(new Insets(0, 0, 0, 0)); // Consistent padding

        // Parse datetime from event and set values
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(event.getDatetime(), formatter);
            datePicker.setValue(dateTime.toLocalDate());
            hourBox.setValue(dateTime.getHour());
            int minute = dateTime.getMinute();
            int roundedMinute = (int) (Math.round(minute / 5.0) * 5);
            if (roundedMinute == 60) roundedMinute = 0;
            minuteBox.setValue(roundedMinute);
        } catch (Exception ex) {
            // Fallback if parsing fails or event.getDatetime() is invalid
            datePicker.setValue(LocalDate.now());
            hourBox.setValue(12);
            minuteBox.setValue(0);
        }

        // Block past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        TextField capacityField = new TextField(String.valueOf(event.getCapacity()));
        capacityField.setPromptText("Capacity"); // Added prompt text for consistency

        TextField locationField = new TextField(event.getLocation());
        locationField.setPromptText("Location"); // Added prompt text for consistency

        Button viewMapBtn = new Button("View Map");
        viewMapBtn.setOnAction(e -> {
            String loc = locationField.getText();
            if (loc != null && !loc.isEmpty()) {
                try {
                    // 1. Open Google Maps in browser
                    String query = URLEncoder.encode(loc, StandardCharsets.UTF_8);
                    // Corrected Google Maps URL
                    Desktop.getDesktop().browse(new URI("https://www.google.com/maps/search/?api=1&query=" + query));
                    
                    // 2. Fetch formatted address from Nominatim and update the form
                    String official = ReverseGeocodingService.getOfficialAddress(loc);
                    if (official != null) {
                        locationField.setText(official); // Autofill official address
                    } else {
                        System.out.println("Could not find location via Nominatim for update.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // Optionally update status label for user
                }
            }
        });

        Button updateBtn = new Button("Update Event");
        Button cancelBtn = new Button("Cancel"); // Added Cancel button for consistency
        Label status = new Label();

        updateBtn.setOnAction(e -> {
            try {
                String title = titleField.getText();
                String desc = descField.getText();
                LocalDate date = datePicker.getValue();
                Integer hour = hourBox.getValue(); // Use Integer for null check
                Integer minute = minuteBox.getValue(); // Use Integer for null check
                String capacityStr = capacityField.getText();
                String location = locationField.getText();

                // Basic validation for required fields
                if (title.isEmpty() || date == null || hour == null || minute == null || capacityStr.isEmpty() || location.isEmpty()) {
                    status.setText("Missing required fields.");
                    return;
                }

                LocalTime time = LocalTime.of(hour, minute);
                LocalDateTime dateTime = LocalDateTime.of(date, time);

                if (dateTime.isBefore(LocalDateTime.now())) {
                    status.setText("Please select a future date and time.");
                    return;
                }

                String datetimeStr = dateTime.format(formatter);
                int capacity;
                try {
                    capacity = Integer.parseInt(capacityStr);
                } catch (NumberFormatException ex) {
                    status.setText("Invalid capacity number.");
                    return;
                }

                boolean updated = OrganizerService.updateEvent(
                        event.getEvent_id(), title, desc, datetimeStr, capacity, location
                );

                if (updated) {
                    stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400));
                } else {
                    status.setText("Failed to update event.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("An error occurred during update.");
            }
        });

        cancelBtn.setOnAction(e -> {
            stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400));
        });

        // --- Layout adjusted for consistency with CreateEventView ---
        getChildren().addAll(
                headerLabel,
                titleField,
                descField,
                new Label("Date"), datePicker,
                new Label("Time (24-hour format)"), timeBox,
                capacityField,
                locationField,
                viewMapBtn,
                new HBox(10, updateBtn, cancelBtn), // Buttons side by side
                status
        );
    }
}