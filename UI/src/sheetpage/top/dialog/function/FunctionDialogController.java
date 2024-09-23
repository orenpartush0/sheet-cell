package sheetpage.top.dialog.function;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class FunctionDialogController {
    @FXML
    private ComboBox<String> functionComboBox;
    @FXML
    private Button createButton;
    @FXML
    private Button cancelButton;
    @FXML
    private VBox vbox;

    private SimpleStringProperty func;

    public boolean isClick = false;

    private Stage dialogStage;

    private final List<SimpleStringProperty> arguments = Arrays.asList(
            new SimpleStringProperty(),
            new SimpleStringProperty(),
            new SimpleStringProperty()
    );

    @FXML
    public void initialize() {
        functionComboBox.setOnAction(event -> {
            createButton.visibleProperty().set(true);
            cancelButton.visibleProperty().set(true);
            vbox.getChildren().clear();
            int numOfArguments = getNumOfArguments(functionComboBox.getValue());
            for (int i = 0; i < numOfArguments; i++) {
                HBox hbox = createButtonAndTextField(arguments.get(i));
                vbox.getChildren().add(hbox);
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private HBox createButtonAndTextField(SimpleStringProperty argument) {

        HBox hbox = new HBox();
        Button addNestedFunctionButton = new Button("Add Nested Function");
        Label arrow = new Label("-->");
        TextField textField = new TextField();
        textField.setPromptText("Argument");
        textField.textProperty().bindBidirectional(argument);
        addNestedFunctionButton.setOnMouseClicked(click -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sheetpage/top/dialog/function/createFunctionDialog.fxml"));
            Parent root = null;
            try {root = loader.load();} catch (IOException ignored) {}
            FunctionDialogController controller = loader.getController();
            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);
            controller.setFunction(argument);
            dialogStage.setTitle("Function Details");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setHeight(240);
            dialogStage.setWidth(450);
            dialogStage.showAndWait();
            textField.setText(controller.getFunWithArguments());
        });
        hbox.getChildren().addAll(addNestedFunctionButton,arrow, textField);
        return hbox;
    }

    public int getNumOfArguments(String functionName) {
        return switch (functionName) {
            case "EQUAL", "NOT", "OR", "AND", "BIGGER", "LESS", "PLUS", "MINUS", "TIMES", "DIVIDE", "MOD", "POW",
                 "PERCENT", "CONCAT" -> 2;
            case "SUB", "IF" -> 3;
            case "SUM", "AVERAGE", "ABS", "REF" -> 1;
            default -> throw new IllegalArgumentException("Unknown function: " + functionName);
        };
    }

    public String getFunWithArguments() {
        switch (functionComboBox.getValue()) {
            case "EQUAL", "NOT", "OR", "AND", "BIGGER", "LESS", "PLUS", "MINUS",
                 "TIMES", "DIVIDE", "MOD", "POW", "PERCENT", "CONCAT" -> {
                return "{" + functionComboBox.getValue() + "," + arguments.getFirst().getValue() + "," + arguments.get(1).getValue() + "}";
            }

            case "SUB", "IF" -> {
                return "{" + functionComboBox.getValue() + "," + arguments.getFirst().getValue() + "," + arguments.get(1).getValue() + "," + arguments.getLast().getValue() + "}";
            }

            case "SUM", "AVERAGE", "ABS", "REF" -> {
                return "{" + functionComboBox.getValue() + "," + arguments.getFirst().getValue() + "}";
            }
            default -> throw new IllegalArgumentException("Unknown function: " + functionComboBox.getValue());
        }
    }

    public void setFunction(SimpleStringProperty function){
        func = function;
    }

    public void onCreateButton(){
        func.set(getFunWithArguments());
        isClick = true;
        dialogStage.close();
    }

    public void onCancel(){
        dialogStage.close();
    }
}
