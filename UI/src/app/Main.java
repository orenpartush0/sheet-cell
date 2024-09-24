package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.LoginController;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login/login.fxml"));
        Parent root = loader.load(); // Load the FXML first
        LoginController loginController = loader.getController(); // Get the controller afterwards

        primaryStage.setTitle("Login");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        loginController.setStage(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}