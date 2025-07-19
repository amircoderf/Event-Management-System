package view;

import model.Organizer;
import api.OrganizerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Event;

import java.util.List;

public class OrganizerDashboardView extends VBox {
    private TableView<Event> upcomingTable;
    private TableView<Event> pastTable;
    private ObservableList<Event> upcomingEvents;
    private ObservableList<Event> pastEvents;

    public OrganizerDashboardView(Stage stage, Organizer organizer) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label welcomeLabel = new Label("Welcome, " + organizer.getName());
        Button logoutBtn = new Button("Logout");
        Button createBtn = new Button("Create New Event");

        upcomingTable = new TableView<>();
        pastTable = new TableView<>();

        upcomingEvents = FXCollections.observableArrayList();
        pastEvents = FXCollections.observableArrayList();

        upcomingTable.setItems(upcomingEvents);
        pastTable.setItems(pastEvents);

        setupTableColumns(upcomingTable);
        setupTableColumns(pastTable);

        HBox actionButtons = new HBox(10);
        Button updateBtn = new Button("Edit Event");
        Button deleteBtn = new Button("Delete Event");
        Button viewFeedbackBtn = new Button("View Feedback");
        Button exportBtn = new Button("Export Participants");
        actionButtons.getChildren().addAll(updateBtn, deleteBtn, viewFeedbackBtn, exportBtn);

        // --- Button handlers ---
        logoutBtn.setOnAction(e -> {
            try {
                new MainApp().start(stage); // restart app by showing login screen
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        createBtn.setOnAction(e -> {
            stage.setScene(new Scene(new CreateEventView(stage, organizer), 500, 450));
        });

        updateBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new UpdateEventView(stage, organizer, selected), 600, 600));
            } else {
                showError("Please select an event to edit.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this event?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (OrganizerService.deleteEvent(selected.getEvent_id())) {
                            upcomingEvents.remove(selected);
                        } else {
                            showError("Failed to delete event.");
                        }
                    }
                });
            } else {
                showError("Please select an event to delete.");
            }
        });

        // ✅ View Feedback for PAST events only
        viewFeedbackBtn.setOnAction(e -> {
            Event selected = pastTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new FeedbackListView(stage, organizer, selected), 500, 400));
            } else {
                showError("Please select a past event to view feedback.");
            }
        });
        
        // ✅ Export Participants from either table
        exportBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                selected = pastTable.getSelectionModel().getSelectedItem();
            }

            if (selected != null) {
                OrganizerService.exportParticipants(selected.getEvent_id());
            } else {
                showError("Please select an event to export participants.");
            }
        });

        // Disable feedback button unless a past event is selected
        pastTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewFeedbackBtn.setDisable(newVal == null);
        });
        
        viewFeedbackBtn.setDisable(true); // initially disabled

        loadEvents(organizer.getOrganizer_id());

        Label upcomingLabel = new Label("Upcoming Events");
        Label pastLabel = new Label("Past Events");

        HBox topBar = new HBox(10, welcomeLabel, logoutBtn);
        getChildren().addAll(
            topBar,
            createBtn,
            upcomingLabel,
            upcomingTable,
            actionButtons,
            new Separator(),
            pastLabel,
            pastTable
        );
    }

    private void setupTableColumns(TableView<Event> table) {
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> data.getValue().titleProperty());

        TableColumn<Event, String> dateCol = new TableColumn<>("Date/Time");
        dateCol.setCellValueFactory(data -> data.getValue().datetimeProperty());

        TableColumn<Event, String> locCol = new TableColumn<>("Location");
        locCol.setCellValueFactory(data -> data.getValue().locationProperty());

        table.getColumns().addAll(titleCol, dateCol, locCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadEvents(int organizerId) {
        List<Event> upcoming = OrganizerService.getUpcomingEventsByOrganizer(organizerId);
        List<Event> past = OrganizerService.getPastEventsByOrganizer(organizerId);
        upcomingEvents.setAll(upcoming);
        pastEvents.setAll(past);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }
}
