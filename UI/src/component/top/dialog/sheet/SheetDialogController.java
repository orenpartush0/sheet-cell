package component.top.dialog.sheet;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SheetDialogController {

    @FXML
    private TextField sheetNameField;
    @FXML
    private TextField numColumnsField;
    @FXML
    private TextField numRowsField;
    @FXML
    private VBox errorBox;

    private Stage dialogStage;
    private boolean okClicked = false;

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handeCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getSheetName() {
        return sheetNameField.getText();
    }

    public int getNumColumns() {
        return Integer.parseInt(numColumnsField.getText());
    }

    public int getNumRows() {
        return Integer.parseInt(numRowsField.getText());
    }

    private boolean isInputValid() {
        errorBox.getChildren().clear();

        boolean isValid = true;

        if (sheetNameField.getText() == null || sheetNameField.getText().isEmpty()) {
            Label errorLabel = new Label("No valid sheet name!");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        try {
            if(Integer.parseInt(numColumnsField.getText()) > 20){
                    throw new RuntimeException("Number of columns must be less than 20");
            };
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("No valid number of columns!");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }
        catch (RuntimeException e){
            Label errorLabel = new Label("Num of Columns must be between 1 and 20!");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        try {
            if(Integer.parseInt(numRowsField.getText()) > 50){
                throw new RuntimeException("Number of rows must be less than 50");
            };
        } catch (NumberFormatException e) {
            Label errorLabel = new Label("No valid number of rows!");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }
        catch (RuntimeException e){
            Label errorLabel = new Label("Num of Rows must be between 1 and 50!");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        return isValid;
    }
}


