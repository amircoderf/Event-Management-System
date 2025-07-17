package view;

import model.Organizer;
import api.OrganizerService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventView extends VBox {
    public CreateEventView(Stage stage, Organizer organizer) {
        setPadding(new Insets(20));
        setSpacing(10);

        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea descField = new TextArea();
        descField.setPromptText("Description");

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select Date");

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

        Button submitBtn = new Button("Create Event");
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



        getChildren().addAll(
            new Label("Create New Event"),
            titleField,
            descField,
            new Label("Date"), datePicker,
            new Label("Time (24-hour format)"), timeBox,
            capacityField,
            locationField,
            submitBtn,
            status
        );
    }
}
