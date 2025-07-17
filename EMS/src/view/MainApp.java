package view;

import api.AuthService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Organizer;
import model.Student;
import org.json.JSONObject;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("EMS Login");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Label roleLabel = new Label("Role:");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("student", "organizer");
        roleCombo.setValue("student");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        Text statusText = new Text();

        grid.add(emailLabel, 0, 0);
        grid.add(emailField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(roleLabel, 0, 2);
        grid.add(roleCombo, 1, 2);
        grid.add(loginBtn, 0, 3);
        grid.add(registerBtn, 1, 3);
        grid.add(statusText, 0, 4, 2, 1);

        loginBtn.setOnAction(e -> {
            String email = emailField.getText();
            String password = passField.getText();
            String role = roleCombo.getValue();

            if (email.isEmpty() || password.isEmpty()) {
                statusText.setText("Please enter both email and password.");
                return;
            }

            try {
                JSONObject json = AuthService.login(email, password, role);

                if ("student".equals(role)) {
                    Student student = new Student(
                        json.getInt("student_id"),
                        json.getString("name"),
                        json.getString("email")
                    );
                    stage.setScene(new Scene(new StudentDashboardView(stage, student), 600, 400));
                } else {
                    Organizer organizer = new Organizer(
                        json.getInt("organizer_id"),
                        json.getString("name"),
                        json.getString("email")
                    );
                    stage.setScene(new Scene(new OrganizerDashboardView(stage, organizer), 800, 500));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusText.setText("Login failed.");
            }
        });

        registerBtn.setOnAction(e -> showRegisterForm(stage));

        Scene scene = new Scene(grid, 400, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void showRegisterForm(Stage stage) {
        GridPane registerGrid = new GridPane();
        registerGrid.setPadding(new Insets(20));
        registerGrid.setHgap(10);
        registerGrid.setVgap(10);

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Label roleLabel = new Label("Role:");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("student", "organizer");
        roleCombo.setValue("student");

        Label extra1Label = new Label();
        TextField extra1Field = new TextField();
        Label extra2Label = new Label();
        TextField extra2Field = new TextField();

        roleCombo.setOnAction(e -> {
            if ("student".equals(roleCombo.getValue())) {
                extra1Label.setText("Student Number:");
                extra2Label.setText("Course:");
            } else {
                extra1Label.setText("Organization:");
                extra2Label.setText("Phone:");
            }
        });
        roleCombo.getOnAction().handle(null); // trigger once initially

        Button submitBtn = new Button("Register");
        Text statusText = new Text();

        registerGrid.add(nameLabel, 0, 0);
        registerGrid.add(nameField, 1, 0);
        registerGrid.add(emailLabel, 0, 1);
        registerGrid.add(emailField, 1, 1);
        registerGrid.add(passLabel, 0, 2);
        registerGrid.add(passField, 1, 2);
        registerGrid.add(roleLabel, 0, 3);
        registerGrid.add(roleCombo, 1, 3);
        registerGrid.add(extra1Label, 0, 4);
        registerGrid.add(extra1Field, 1, 4);
        registerGrid.add(extra2Label, 0, 5);
        registerGrid.add(extra2Field, 1, 5);
        registerGrid.add(submitBtn, 0, 6);
        registerGrid.add(statusText, 0, 7, 2, 1);

        submitBtn.setOnAction(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = passField.getText();
            String role = roleCombo.getValue();
            String extra1 = extra1Field.getText();
            String extra2 = extra2Field.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || extra1.isEmpty() || extra2.isEmpty()) {
                statusText.setText("Please fill all fields.");
                return;
            }

            try {
                boolean success = AuthService.register(name, email, password, role, extra1, extra2);
                if (success) {
                    statusText.setText("Registration successful! Return to login.");
                } else {
                    statusText.setText("Registration failed.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                statusText.setText("Error during registration.");
            }
        });

        stage.setScene(new Scene(registerGrid, 450, 350));
    }

    public static void main(String[] args) {
        launch();
    }
}