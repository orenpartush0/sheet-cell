package component.top.dialog.filter;

import component.app.AppController;
import component.top.dialog.range.RangeDialogController;
import dto.SheetDto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static component.top.TopController.createNewSheetInDifferentWindows;

public class FilterDialogController {

    private AppController appController;
    private int numOfRows;
    private int numOfCols;
    private boolean needToBeUpdated = true;

    Map<Integer, List<String>> colsValues;

    @FXML public ListView<String> valuesListView;
    @FXML public TextField startPointField;
    @FXML public TextField endPointField;
    @FXML private Stage dialogStage;
    @FXML private VBox errorBox;
    @FXML private ComboBox keyColumnComboBox;
    @FXML private Button applyButton;

    @FXML
    public void initialize(){
        startPointField.textProperty().addListener((observable, oldValue, newValue) -> {
            needToBeUpdated = true;
        });
        endPointField.textProperty().addListener((observable, oldValue, newValue) -> {
            needToBeUpdated = true;
        });
    }

    private final Map<Integer, Map<String, SimpleBooleanProperty>> checked = new HashMap<>();

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Coordinate getStartPoint() {
        return CoordinateFactory.getCoordinate(startPointField.getText());
    }

    public Coordinate getEndPoint() {
        return CoordinateFactory.getCoordinate(endPointField.getText());
    }

    private int getSelectedColNumber(){return keyColumnComboBox.getSelectionModel().getSelectedIndex() + 1; }

    private String getColLetter(int colNum) { return String.valueOf((char) ('A' + colNum - 1)); }

    public void ApplyFilter() {
        Map<Integer, List<String>> filters = new HashMap<>();

        checked.forEach((key, map) -> map.forEach((val, isChecked)-> {
            if(isChecked.getValue()){
                if(!filters.containsKey(key)){
                    filters.put(key, new ArrayList<>());
                }

                filters.get(key).add(val);
            }
        }));

        SheetDto sheet = appController.applyFilter(getRange(),filters);
        createNewSheetInDifferentWindows(sheet);
    }

    private boolean isInputValid() {
        errorBox.getChildren().clear();
        boolean isValid = true;

        if (!CoordinateFactory.isValidCoordinate(startPointField.getText())) {
            Label errorLabel = new Label("Invalid start point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }
        if (!CoordinateFactory.isValidCoordinate(endPointField.getText())) {
            Label errorLabel = new Label("Invalid end point!\n");
            errorLabel.setStyle("-fx-text-fill: red;");
            errorBox.getChildren().add(errorLabel);
            isValid = false;
        }

        if (CoordinateFactory.isValidCoordinate(startPointField.getText()) && CoordinateFactory.isValidCoordinate(endPointField.getText())) {
            if (!RangeDialogController.isRangeValid(startPointField.getText(), endPointField.getText())) {
                Label errorLabel = new Label("Start point should be less than or equal to end point!\n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }

            if (!RangeDialogController.isInBoundaries(endPointField.getText(), numOfRows, numOfCols)) {
                Label errorLabel = new Label("Range out of boundaries \n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }

            if(getSelectedColNumber() > getRange().endCellCoordinate().col() || getSelectedColNumber() < getRange().startCellCoordinate().col()){
                Label errorLabel = new Label("Column not in Range \n");
                errorLabel.setStyle("-fx-text-fill: red;");
                errorBox.getChildren().add(errorLabel);
                isValid = false;
            }
        }

        applyButton.disableProperty().set(!isValid);
        return isValid;
    }

    public void fillData(){
        IntStream.range(1, numOfCols + 1).forEach(col -> keyColumnComboBox.getItems().add(getColLetter(col)) );
        IntStream.range(1, numOfRows + 1).forEach(row -> checked.put(row,new HashMap<>()));

        keyColumnComboBox.setOnAction(actionEvent -> {
            if (isInputValid()) {
                colsValues = needToBeUpdated
                        ? appController.getValuesInColumns(getRange())
                        : colsValues;
                valuesListView.getItems().clear();
                setColValue(colsValues.get(getSelectedColNumber()));
            }
        });
    }

    public void setColValue(List<String> valuesList) {

        ObservableList<String> observableList = FXCollections.observableArrayList(valuesList);
        valuesListView.setItems(observableList);
        valuesListView.setCellFactory(param -> new ListCell<>() {
            private final Label label = new Label();
            private final CheckBox checkBox = new CheckBox();
            private final HBox hbox = new HBox(label, checkBox);

            {
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    SimpleBooleanProperty selected = checked.get(getSelectedColNumber()).get(item) == null
                            ? new SimpleBooleanProperty(false)
                            : checked.get(getSelectedColNumber()).get(item);

                    checked.get(getSelectedColNumber()).put(item,selected);
                    checkBox.setSelected(checked.get(getSelectedColNumber()).get(item).getValue());

                    checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                        checked.get(getSelectedColNumber()).put(item,new SimpleBooleanProperty(newValue));
                    });

                    setGraphic(hbox);
                }
            }
        });
    }


    private Range getRange(){
        Coordinate startPointCoordinate = getStartPoint();
        Coordinate endPointCoordinate = getEndPoint();
        return new Range("",startPointCoordinate,endPointCoordinate);
    }

    @FXML
    public void handeCancel() {dialogStage.close();}

    public void setBoundaries(int _numOfRows, int _numOfCols) {
        numOfCols = _numOfCols;
        numOfRows = _numOfRows;
    }

}