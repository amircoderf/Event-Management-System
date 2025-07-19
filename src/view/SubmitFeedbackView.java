package view;

import api.StudentService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Event;
import model.Student;

public class SubmitFeedbackView extends VBox {
    public SubmitFeedbackView(Stage stage, Student student, Event event) {
        setPadding(new Insets(20));
        setSpacing(10);

        Label header = new Label("Feedback for " + event.getTitle());
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 5);
        TextArea commentArea = new TextArea();
        commentArea.setPromptText("Write your comment...");

        Button submitBtn = new Button("Submit Feedback");
        Button backBtn = new Button("Back");
        Label status = new Label();

        submitBtn.setOnAction(e -> {
            boolean ok = StudentService.submitFeedback(
                student.getStudent_id(),
                event.getEvent_id(),
                ratingSpinner.getValue(),
                commentArea.getText()
            );
            if (ok) {
                stage.setScene(new Scene(new StudentDashboardView(stage, student), 600, 400));
            } else {
                status.setText("Submission failed or already submitted.");
            }
        });

        backBtn.setOnAction(e -> {
            stage.setScene(new Scene(new StudentDashboardView(stage, student), 600, 400));
        });

        getChildren().addAll(
            header,
            new Label("Rating (1-5):"),
            ratingSpinner,
            commentArea,
            new HBox(10, submitBtn, backBtn),
            status
        );
    }
}
