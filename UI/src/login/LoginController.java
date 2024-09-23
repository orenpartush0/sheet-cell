package login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button submitButton;

    @FXML
    private Label errorMessage;

    @FXML
    public void initialize() {
        submitButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            displayError("Username cannot be empty");
        } else {
            clearError();
            submitButton.setDisable(true);
        }
    }

    private void displayError(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
    }

    private void clearError() {
        errorMessage.setText("");
        errorMessage.setVisible(false);
    }
}
