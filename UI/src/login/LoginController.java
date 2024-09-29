package login;

import dashboard.DashBoardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import util.HttpClientUtil;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private Button submitButton;

    @FXML
    private Label errorMessage;

    private Stage stage;

    @FXML
    public void initialize() {
        submitButton.setOnAction(e -> {
            try { handleLogin(); } catch (IOException ex) {
                displayError("Unexpected error occurred. Please try again.");
            }
        });
    }

    public void setStage(Stage _stage) {
        stage = _stage;
    }

    private void handleLogin() throws IOException {
        submitButton.setDisable(true);
        String userName = usernameField.getText();
        if (userName.isEmpty()) {
            displayError("Username cannot be empty");
        } else {
            Response response =HttpClientUtil.runSync("/user?userName=" + userName, "PUT",null);
            if (!response.isSuccessful()) {
                submitButton.setDisable(false);
                clearError();
                displayError("Login failed. User already exist.");
            }else {
                stage.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/DashBoard.fxml"));
                Parent root = loader.load();
                DashBoardController dashBoardcontroller = loader.getController();
                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("DashBoard");
                dashboardStage.initModality(Modality.WINDOW_MODAL);
                dashboardStage.setScene(new Scene(root));
                dashBoardcontroller.setStage(dashboardStage);
                dashBoardcontroller.setName(usernameField.getText());
                dashboardStage.showAndWait();
            }
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
