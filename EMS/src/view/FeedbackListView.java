package view;

import model.Organizer;
import api.OrganizerService;
import com.google.gson.JsonObject;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;


import java.util.List;

public class FeedbackListView extends VBox {
    public FeedbackListView(Stage stage,Organizer organizer, Event event) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label header = new Label("Feedback for: " + event.getTitle());
        ListView<String> listView = new ListView<>();

        List<JsonObject> feedbacks = OrganizerService.getFeedbackByEvent(event.getEvent_id());
        for (JsonObject f : feedbacks) {
            String entry = f.get("student_name").getAsString() + " - Rating: " + f.get("rating").getAsInt();
            if (f.has("comment") && !f.get("comment").getAsString().isEmpty()) {
                entry += "\nComment: " + f.get("comment").getAsString();
            }
            listView.getItems().add(entry);
        }

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 600, 400)));

        getChildren().addAll(header, listView, backBtn);
    }
}
