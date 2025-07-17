package model;

public class Student {
    private int user_id;
    private int student_id;
    private String name;
    private String email;
    
    public Student(int studentId, String name, String email) {
        this.student_id = studentId;
        this.name = name;
        this.email = email;
    }


    public int getUser_id() { return user_id; }
    public int getStudent_id() { return student_id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}