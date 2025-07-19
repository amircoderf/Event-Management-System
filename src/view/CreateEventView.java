package view;

import model.Organizer;
import api.OrganizerService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime; // Import LocalDateTime
import java.time.LocalTime;
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import api.ReverseGeocodingService;
import javafx.application.Platform;
import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventView extends VBox {
	
	private double selectedLat = 0;
	private double selectedLon = 0;
	private Label addressInfo = new Label("Formatted address will appear here");
	
    public CreateEventView(Stage stage, Organizer organizer) {
        setPadding(new Insets(20));
        setSpacing(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Also block future dates if they are before today's date
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;"); // invalid date will be red
                }
            }
        });


        ComboBox<Integer> hourBox = new ComboBox<>();
        for (int i = 0; i < 24; i++) hourBox.getItems().add(i);
        hourBox.setPromptText("Hour");

        ComboBox<Integer> minuteBox = new ComboBox<>();
        for (int i = 0; i < 60; i += 5) minuteBox.getItems().add(i);
        minuteBox.setPromptText("Minute");

        HBox timeBox = new HBox(10, hourBox, minuteBox);
        timeBox.setPadding(new Insets(0, 0, 0, 0));

        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");

        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        
        Label coordLabel = new Label();
        
       
        
        Button viewMapBtn = new Button("View Map");
        viewMapBtn.setOnAction(e -> {
        	String loc = locationField.getText();
        	if (loc != null && !loc.isEmpty()) {
        	try {
        	// 1. Open Google Maps in browser
        	String query = URLEncoder.encode(loc, StandardCharsets.UTF_8);
        	// Corrected URL: "http://maps.google.com/maps?q="
        	Desktop.getDesktop().browse(new URI("http://maps.google.com/maps?q=" + query));
        	// 2. Fetch formatted address from Nominatim and update the form
            String official = ReverseGeocodingService.getOfficialAddress(loc);
            if (official != null) {
                locationField.setText(official); // ✅ Autofill official address
            } else {
                System.out.println("Could not find location via Nominatim.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    });

        Button submitBtn = new Button("Create Event");
        Button cancelBtn = new Button("Cancel");
        Label status = new Label();

        submitBtn.setOnAction(e -> {
            String title = titleField.getText();
            String desc = descField.getText();
            LocalDate date = datePicker.getValue();
            Integer hour = hourBox.getValue();
            Integer minute = minuteBox.getValue();
            String capacityStr = capacityField.getText();
            String location = locationField.getText();

            if (title.isEmpty() || date == null || hour == null || minute == null || capacityStr.isEmpty() || location.isEmpty()) {
                status.setText("Missing required fields");
                return;
            }

            // Combine date and time to create LocalDateTime for future check
            LocalDateTime selectedDateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
            //Add check for future date and time
            if (selectedDateTime.isBefore(LocalDateTime.now())) {
                status.setText("Please select a future date and time.");
                return;
            }
            // --- End of new check ---


            String datetime = date.toString() + " " + String.format("%02d:%02d:00", hour, minute);

            int capacity;
            try {
                capacity = Integer.parseInt(capacityStr);
            } catch (NumberFormatException ex) {
                status.setText("Invalid capacity number");
                return;
            }

            boolean success = OrganizerService.createEvent(
                title, desc, datetime, capacity, location, organizer.getOrganizer_id()
            );

            if (success) {
                stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400));
            } else {
                status.setText("Failed to create event");
            }
        });

        cancelBtn.setOnAction(e -> {
            stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400));
        });

        getChildren().addAll(
        	    new Label("Create New Event"),
        	    titleField,
        	    descField,
        	    new Label("Date"), datePicker,
        	    new Label("Time (24-hour format)"), timeBox,
        	    capacityField,
        	    locationField,
        	    viewMapBtn,
        	    new HBox(10, submitBtn, cancelBtn),  // Add both buttons side by side
        	    status
        	);


    }
}