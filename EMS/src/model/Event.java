package model;

import javafx.beans.property.SimpleStringProperty;

public class Event {
    private int event_id;
    private SimpleStringProperty title;
    private SimpleStringProperty description;
    private SimpleStringProperty datetime;
    private SimpleStringProperty location;
    private int capacity;

    public Event(int event_id, String title, String description, String datetime, String location, int capacity) {
        this.event_id = event_id;
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.datetime = new SimpleStringProperty(datetime);
        this.location = new SimpleStringProperty(location);
        this.capacity = capacity;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getTitle() {
        return title.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getDatetime() {
        return datetime.get();
    }

    public String getLocation() {
        return location.get();
    }

    public int getCapacity() {
        return capacity;
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public SimpleStringProperty datetimeProperty() {
        return datetime;
    }

    public SimpleStringProperty locationProperty() {
        return location;
    }
} 
