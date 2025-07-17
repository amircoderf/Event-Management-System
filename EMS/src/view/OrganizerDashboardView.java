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
        actionButtons.getChildren().addAll(updateBtn, deleteBtn, viewFeedbackBtn);

        createBtn.setOnAction(e -> {
            stage.setScene(new Scene(new CreateEventView(stage, organizer), 500, 450));
        });

        updateBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new UpdateEventView(stage, organizer, selected), 500, 450));
            }
        });

        deleteBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected != null && OrganizerService.deleteEvent(selected.getEvent_id())) {
                upcomingEvents.remove(selected);
            }
        });

        viewFeedbackBtn.setOnAction(e -> {
            Event selected = upcomingTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.setScene(new Scene(new FeedbackListView(stage, organizer, selected), 500, 400));
            }
        });

        loadEvents(organizer.getOrganizer_id());

        Label upcomingLabel = new Label("Upcoming Events");
        Label pastLabel = new Label("Past Events");

        getChildren().addAll(
            welcomeLabel,
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
}
