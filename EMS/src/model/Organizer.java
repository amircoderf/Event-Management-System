package model;

public class Organizer {
    private int user_id;
    private int organizer_id;
    private String name;
    private String email;
    private String role;
    
    public Organizer(int organizerId, String name, String email) {
        this.organizer_id = organizerId;
        this.name = name;
        this.email = email;
    }
    
    public int getUser_id() { return user_id; }
    public int getOrganizer_id() { return organizer_id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}