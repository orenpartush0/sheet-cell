package component.top.dialog.range;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

public class RangeDialogController {

    @FXML
    private TextField rangeNameField;

    @FXML
    private TextField startPointField;

    @FXML
    private TextField endPointField;

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
    private void handeCancel(){
        dialogStage.close();
    }


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getRangeName() {
        return rangeNameField.getText();
    }

    public Coordinate getStartPoint() {
        return CoordinateFactory.getCoordinate(startPointField.getText());
    }

    public Coordinate getEndPoint() {
        return CoordinateFactory.getCoordinate(endPointField.getText());
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (rangeNameField.getText() == null || rangeNameField.getText().isEmpty()) {
            errorMessage += "No valid range name!\n";
        }

        if (!CoordinateFactory.isValidCoordinate(startPointField.getText())) {
            errorMessage += "No valid start point!\n";
        }

        if (!CoordinateFactory.isValidCoordinate(endPointField.getText())) {
            errorMessage += "No valid end point!\n";
        }

        if (CoordinateFactory.isValidCoordinate(startPointField.getText()) && CoordinateFactory.isValidCoordinate(endPointField.getText())) {
            if (!isRangeValid(startPointField.getText(), endPointField.getText())) {
                errorMessage += "Start point should be less than or equal to end point!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            throw new RuntimeException(errorMessage);
        }
    }

    private boolean isRangeValid(String startPoint, String endPoint) {
        Coordinate startPointCoordinate = CoordinateFactory.getCoordinate(startPoint);
        Coordinate endPointCoordinate = CoordinateFactory.getCoordinate(endPoint);

        return startPointCoordinate.row() <= endPointCoordinate.row()
                && startPointCoordinate.col() <= endPointCoordinate.col();
    }

}
