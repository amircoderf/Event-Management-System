package view;

import api.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Student;

import java.util.List;

public class StudentPastEventsView extends VBox {
    public StudentPastEventsView(Stage stage, Student student) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label header = new Label("Past Events Attended");
        TableView<Event> table = new TableView<>();
        ObservableList<Event> pastList = FXCollections.observableArrayList();

        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Event, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(data -> data.getValue().datetimeProperty());

        TableColumn<Event, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(data -> data.getValue().locationProperty());

        table.getColumns().addAll(titleCol, dateCol, locCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        pastList.setAll(StudentService.getPastEvents(student.getStudent_id()));
        table.setItems(pastList);

        // === Feedback Button ===
        Button feedbackBtn = new Button("Give Feedback");
        feedbackBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new SubmitFeedbackView(stage, student, selected), 400, 300));
            } else {
                showError("Please select a past event to give feedback.");
            }
        });

        // === Back Button ===
        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(new Scene(new StudentDashboardView(stage, student), 600, 400)));

        HBox buttons = new HBox(10, feedbackBtn, backBtn);

        getChildren().addAll(header, table, buttons);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }
}
