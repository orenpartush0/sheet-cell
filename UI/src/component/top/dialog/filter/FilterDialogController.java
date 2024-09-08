package component.top.dialog.filter;

import component.app.AppController;
import dto.CellDto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilterDialogController {
    @FXML public Button okButton;
    @FXML private AppController appController;
    @FXML public TextField startPointField;
    @FXML public TextField endPointField;
    @FXML public TextField KeyColumnFiled;
    @FXML public ListView<String> valuesListView;
    @FXML public Button ApplyFilter;
    @FXML public Button cancelButton;
    @FXML private Stage dialogStage;

    private boolean firstTime = true;
    private final Map<String, SimpleBooleanProperty> checked = new HashMap<>();

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

        ObservableList<String> observableList = FXCollections.observableArrayList(valuesList);
        valuesListView.setItems(observableList);

        valuesListView.setCellFactory(param -> new ListCell<String>() {
            private final Label label = new Label();
            private final CheckBox checkBox = new CheckBox();
            private final HBox hbox = new HBox(label, checkBox);

            {
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);
                    SimpleBooleanProperty selected = new SimpleBooleanProperty();
                    checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            selected.set(newValue);
                        }
                    });
                    checked.put(item, selected);
                    setGraphic(hbox);
                }
            }
       });
    }

            private void clearValuesComboBox() {
                valuesListView.getItems().clear();
            }

            public void handelOK() {
                if (isInputValid()) {
                    keyColumnChanged();

                }
            }

            @FXML
            public void keyColumnChanged() {
                clearValuesComboBox();
                Coordinate startPointCoordinate = getStartPoint();
                Coordinate endPointCoordinate = getEndPoint();
                String col = getKeyColumnFiled();
                if (firstTime) {
                    setValuesComboBox(appController.createFilter(startPointCoordinate, endPointCoordinate, col));
                    firstTime = false;
                } else {
                    setValuesComboBox(appController.setFilterCol(col));
                }
            }

            public void ApplyFilter() {
                List<String> valuesList = new ArrayList<>();
                checked.forEach((val, checked) -> {
                    if (checked.getValue()) {
                        valuesList.add(val);
                    }
                });
                List<CellDto> temp = appController.applyFilter(valuesList);
                List<Coordinate> coordinates = new ArrayList<>();
                temp.forEach(c -> coordinates.add(c.coordinate()));
                appController.PaintCells(coordinates, "pink");
            }

            @FXML
            public void handeCancel() {
                dialogStage.close();
            }


        }