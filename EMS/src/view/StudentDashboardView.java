package view;

import api.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Student;

import java.util.List;

public class StudentDashboardView extends VBox {
    private TableView<Event> table;
    private ObservableList<Event> eventList;

    public StudentDashboardView(Stage stage, Student student) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label welcomeLabel = new Label("Welcome, " + student.getName());

        table = new TableView<>();
        eventList = FXCollections.observableArrayList();
        table.setItems(eventList);

        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Event, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(data -> data.getValue().datetimeProperty());

        TableColumn<Event, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(data -> data.getValue().locationProperty());

        table.getColumns().addAll(titleCol, dateCol, locCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button registerBtn = new Button("Register Events");
        Button feedbackBtn = new Button("Give Feedback");
        Button viewPastBtn = new Button("View Past Events");

        registerBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                StudentService.registerForEvent(student.getStudent_id(), selected.getEvent_id());
                loadEvents(student.getStudent_id());
            }
        });

        feedbackBtn.setOnAction(e -> {
            Event selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new SubmitFeedbackView(stage, student, selected), 400, 300));
            }
        });

        viewPastBtn.setOnAction(e -> {
            stage.setScene(new Scene(new StudentPastEventsView(stage, student), 600, 400));
        });

        loadEvents(student.getStudent_id());

        getChildren().addAll(welcomeLabel, table, registerBtn,feedbackBtn, viewPastBtn);
    }

    private void loadEvents(int studentId) {
        List<Event> events = StudentService.getMyEvents(studentId);
        eventList.setAll(events);
    }
}