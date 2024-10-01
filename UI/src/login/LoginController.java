package login;

import com.jfoenix.controls.JFXTextField;
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
import sheetpage.app.AppController;
import util.HttpClientUtil;

import java.io.IOException;


public class LoginController {

    @FXML
    private JFXTextField usernameField;

    @FXML
    private Label errorMessage;

    private Stage stage;

    public void setStage(Stage _stage) {
        stage = _stage;
    }

    @FXML
    public void handleLogin() throws IOException {
        String userName = usernameField.getText();
        if (userName.isEmpty()) {
            AppController.showError("Username cannot be empty");
        } else {
            Response response =HttpClientUtil.runSync("/user?userName=" + userName, "PUT",null);
            if (!response.isSuccessful()) {
                AppController.showError("Login failed. User already exist.");
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

}
