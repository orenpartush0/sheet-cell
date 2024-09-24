package login;

import Connector.Connector;
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
                throw new RuntimeException(ex);}
        });
    }

    public void setStage(Stage _stage) {
        stage = _stage;
    }

    private void handleLogin() throws IOException {
        String userName = usernameField.getText();
            if (userName.isEmpty()) {
                displayError("Username cannot be empty");
            } else {
                if(Connector.Login(userName)){
                    this.stage.close();
                    submitButton.disableProperty().set(true);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/DashBoard.fxml"));
                    Parent root = loader.load();
                    DashBoardController dashBoardcontroller = loader.getController();
                    Stage stage = new Stage();
                    stage.setTitle("DashBoard");
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(new Scene(root));
                    dashBoardcontroller.setStage(stage);
                    dashBoardcontroller.setName(userName);
                    stage.showAndWait();
                }
                else{
                    clearError();
                    displayError("User already exists");
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
