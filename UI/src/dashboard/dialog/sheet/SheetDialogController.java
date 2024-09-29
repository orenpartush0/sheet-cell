package dashboard.dialog.sheet;

import constant.Constants;
import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.HttpClientUtil;

import java.io.IOException;

import static constant.UIConstants.PUT;

public class SheetDialogController {

    @FXML private TextField sheetNameField;
    @FXML private TextField numColumnsField;
    @FXML private TextField numRowsField;
    @FXML private VBox errorBox;

    private Stage dialogStage;

    @FXML
    private void handleOk() throws IOException {
        if (isInputValid()) {
            String sheetName = getSheetName();
            int numColumns = getNumColumns();
            int numRows = getNumRows();

            boolean isSuccessful = HttpClientUtil.runSync(Constants.SHEET,PUT,
                    new SheetDto(sheetName,1,numColumns,numRows,100,30)).isSuccessful();

            if(isSuccessful){
                dialogStage.close();
            };

            Label errorLabel = new Label("Sheet name already exists");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
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
            }
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
            }
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


