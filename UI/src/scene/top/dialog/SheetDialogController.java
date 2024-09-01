package scene.top.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SheetDialogController {

    @FXML
    private TextField sheetNameField;

    @FXML
    private TextField numColumnsField;

    @FXML
    private TextField numRowsField;

    @FXML
    private Button okButton;

    @FXML
    private Button cancelButton;

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
        String errorMessage = "";

        if (sheetNameField.getText() == null || sheetNameField.getText().length() == 0) {
            errorMessage += "No valid sheet name!\n";
        }

        try {
            Integer.parseInt(numColumnsField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "No valid number of columns!\n";
        }

        try {
            Integer.parseInt(numRowsField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "No valid number of rows!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
}

