package view;

import api.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Student;

import java.util.List;

public class StudentDashboardView extends VBox {
    private TableView<Event> allEventsTable;
    private ObservableList<Event> allEvents;

    private TableView<Event> registeredTable;
    private ObservableList<Event> registeredEventList;

    public StudentDashboardView(Stage stage, Student student) {
        setPadding(new Insets(20));
        setSpacing(15);

        Label welcomeLabel = new Label("Welcome, " + student.getName());
        Button logoutBtn = new Button("Logout");

        // === All Events Table ===
        allEvents = FXCollections.observableArrayList();
        allEventsTable = new TableView<>();
        allEventsTable.setItems(allEvents);
        allEventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Event, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(data -> data.getValue().datetimeProperty());

        TableColumn<Event, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(data -> data.getValue().locationProperty());

        TableColumn<Event, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());

        allEventsTable.getColumns().addAll(titleCol, dateCol, locCol, statusCol);

        // === Registered Events Table ===
        registeredEventList = FXCollections.observableArrayList();
        registeredTable = new TableView<>();
        registeredTable.setItems(registeredEventList);
        registeredTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Event, String> regTitleCol = new TableColumn<>("Title");
        regTitleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Event, String> regDateCol = new TableColumn<>("Date/Time");
        regDateCol.setCellValueFactory(data -> data.getValue().datetimeProperty());

        TableColumn<Event, String> regLocCol = new TableColumn<>("Location");
        regLocCol.setCellValueFactory(data -> data.getValue().locationProperty());

        registeredTable.getColumns().addAll(regTitleCol, regDateCol, regLocCol);
        
        allEventsTable.setRowFactory(tv -> {
            TableRow<Event> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Event selected = row.getItem();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Event Details");
                    alert.setHeaderText(selected.getTitle());
                    alert.setContentText(
                        "Date/Time: " + selected.getDatetime() + "\n" +
                        "Location: " + selected.getLocation() + "\n\n" +
                        "Description:\n" + selected.getDescription()
                    );
                    alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                    alert.showAndWait();
                }
            });
            return row;
        });


        // === Buttons ===
        Button registerBtn = new Button("Register");
        Button viewPastBtn = new Button("View Past Events");

        logoutBtn.setOnAction(e -> {
            try {
                new MainApp().start(stage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registerBtn.setOnAction(e -> {
            Event selected = allEventsTable.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.getStatus().equals("Registered")) {
                boolean success = StudentService.registerForEvent(student.getStudent_id(), selected.getEvent_id());
                if (success) {
                    showInfo("Successfully registered for the event!");
                    loadEvents(student.getStudent_id());
                    loadRegisteredEvents(student.getStudent_id());
                } else {
                    showError("Registration failed.");
                }
            } else {
                showError("Please select an event you haven't registered for.");
            }
        });

        viewPastBtn.setOnAction(e -> {
            stage.setScene(new Scene(new StudentPastEventsView(stage, student), 600, 400));
        });

        HBox topBar = new HBox(10, welcomeLabel, logoutBtn);
        HBox buttons = new HBox(10, registerBtn, viewPastBtn);

        Label regLabel = new Label("My Registered Events:");
        Label allEventsLabel = new Label("Available Events:");

        getChildren().addAll(topBar, allEventsLabel, allEventsTable, buttons, regLabel, registeredTable);

        loadEvents(student.getStudent_id());
        loadRegisteredEvents(student.getStudent_id());
    }

    private void loadEvents(int studentId) {
        List<Event> available = StudentService.getAvailableEvents(studentId);
        allEvents.setAll(available);
    }

    private void loadRegisteredEvents(int studentId) {
        List<Event> registered = StudentService.getRegisteredEvents(studentId);
        for (Event e : registered) {
            e.setStatus("Registered");
        }
        registeredEventList.setAll(registered);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.show();
    }
}
