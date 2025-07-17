package view;

import api.OrganizerService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Organizer;

public class UpdateEventView extends VBox {
    public UpdateEventView(Stage stage, Organizer organizer, Event event) {
        setPadding(new Insets(20));
        setSpacing(10);

        TextField titleField = new TextField(event.getTitle());
        TextArea descField = new TextArea(event.getDescription());
        TextField datetimeField = new TextField(event.getDatetime());
        TextField capacityField = new TextField(String.valueOf(event.getCapacity()));
        TextField locationField = new TextField(event.getLocation());

        Button updateBtn = new Button("Update Event");
        Label status = new Label();

        updateBtn.setOnAction(e -> {
            String title = titleField.getText();
            String desc = descField.getText();
            String datetime = datetimeField.getText();
            String location = locationField.getText();
            int capacity = Integer.parseInt(capacityField.getText());

            boolean updated = OrganizerService.updateEvent(event.getEvent_id(), title, desc, datetime, capacity, location);
            if (updated) {
                stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400));
            } else {
                status.setText("Failed to update event");
            }
        });

        getChildren().addAll(new Label("Update Event"), titleField, descField, datetimeField, capacityField, locationField, updateBtn, status);
    }
}