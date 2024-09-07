package component.top.dialog.filter;

import component.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

import java.util.List;

public class FilterDialogController {
    private AppController appController;
    public TextField startPointField;
    public TextField endPointField;
    public TextField KeyColumnFiled;
    public ComboBox ValuesComboBox;
    public TextField SelectedValues;
    public Button ApplyFilter;
    public Button cancelButton;
    private Stage dialogStage;

    private boolean firstTime = true;

    public  void handelOK(){
        if(isInputValid()){
            keyColumnChanged();
        }
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private boolean isRangeValid(String startPoint, String endPoint) {
        Coordinate startPointCoordinate = CoordinateFactory.getCoordinate(startPoint);
        Coordinate endPointCoordinate = CoordinateFactory.getCoordinate(endPoint);

        return startPointCoordinate.row() <= endPointCoordinate.row()
                && startPointCoordinate.col() <= endPointCoordinate.col();
    }

    public Coordinate getStartPoint() {
        return CoordinateFactory.getCoordinate(startPointField.getText());
    }

    public Coordinate getEndPoint() {
        return CoordinateFactory.getCoordinate(endPointField.getText());
    }

    public String getKeyColumnFiled() { return KeyColumnFiled.getText();}

    private boolean isInputValid() {
        String errorMessage = "";

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

    public void setValuesComboBox (List<String> valuesList){
        ValuesComboBox.getItems().addAll(valuesList);
    }

    private void clearValuesComboBox(){
        ValuesComboBox.getItems().clear();
    }
     @FXML
    public void keyColumnChanged(){
        clearValuesComboBox();
        Coordinate startPointCoordinate = getStartPoint();
        Coordinate endPointCoordinate = getEndPoint();
        String col = getKeyColumnFiled();
        if(firstTime){
            setValuesComboBox(appController.createFilter(startPointCoordinate, endPointCoordinate, col));
            firstTime = false;
        }
        else{
            setValuesComboBox(appController.setFilterCol(col));
        }
     }

     public void ApplyFilter(){

     }

     @FXML
    public void handeCancel(){
        dialogStage.close();
     }


}
