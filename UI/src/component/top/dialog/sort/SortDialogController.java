package component.top.dialog.sort;

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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.range.Range;

import java.util.*;
import java.util.stream.IntStream;

public class SortDialogController {

    private AppController appController;
    private int numOfRows;
    private int numOfCols;

    Queue<String> pickedCols = new LinkedList<>();

    @FXML public ListView<String> valuesListView;
    @FXML public TextField startPointField;
    @FXML public TextField endPointField;
    @FXML private Stage dialogStage;
    @FXML private VBox errorBox;
    @FXML private Button applyButton;


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private Coordinate getStartPoint() {
        return CoordinateFactory.getCoordinate(startPointField.getText());
    }

    private Coordinate getEndPoint() {
        return CoordinateFactory.getCoordinate(endPointField.getText());
    }


    private String getColLetter(int colNum) { return String.valueOf((char) ('A' + colNum - 1)); }

    @FXML
    public void ApplySort() {
       SheetDto sheet = appController.applySort(pickedCols,getRange());
       appController.createNewSheetInDifferentWindows(sheet);
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

        }

        applyButton.disableProperty().set(!isValid);
        return isValid;
    }

    public void setColValue(List<String> valuesList) {
        ObservableList<String> observableList = FXCollections.observableArrayList(valuesList);

        valuesListView.setItems(observableList);
        valuesListView.setCellFactory(param -> new ListCell<>() {
            private final Label label = new Label();
            private final CheckBox checkBox = new CheckBox();
            private final StackPane circlePane = new StackPane(); // StackPane to layer the circle and number
            private final HBox hbox = new HBox(circlePane, label, checkBox);

            {
                hbox.setSpacing(10);
                hbox.setAlignment(Pos.CENTER_LEFT);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

                checkBox.setOnAction(event -> {
                    String itemText = getItem();

                    if (checkBox.isSelected()) {
                        pickedCols.add(itemText);
                    } else {
                        pickedCols.remove(itemText);
                    }

                    valuesListView.refresh();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty); // Ensure the default behavior happens

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    label.setText(item);

                    Circle circle = new Circle(10); // Circle with a radius of 10
                    circle.setFill(Color.LIGHTBLUE); // Set circle color

                    int queuePosition = -1;
                    if (pickedCols.contains(item)) {
                        queuePosition = new ArrayList<>(pickedCols).indexOf(item) + 1;
                    }

                    Label queueLabel = new Label(queuePosition > 0 ? String.valueOf(queuePosition) : "");
                    queueLabel.setTextFill(Color.BLACK); // Set text color
                    queueLabel.setStyle("-fx-font-weight: bold;"); // Bold font for the number

                    circlePane.getChildren().setAll(circle, queueLabel); // Stack the label on top of the circle
                    circlePane.setAlignment(Pos.CENTER); // Center the label inside the circle

                    checkBox.setSelected(pickedCols.contains(item));

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
    public void handeCancel() { dialogStage.close(); }

    public void setBoundaries(int _numOfCols,int _numOfRows){
        numOfCols = _numOfCols;
        numOfRows = _numOfRows;
    }

    private void setCols() {
        setColValue(IntStream.range(getRange().startCellCoordinate().col(),getRange().endCellCoordinate().col() + 1).mapToObj(this::getColLetter).toList());
    }

    @FXML
    public void handleOk(){
        if(isInputValid()){
            setCols();
        }
    }

}