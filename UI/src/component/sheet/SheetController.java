package component.sheet;

import component.sheet.Enum.PropType;
import dto.SheetDto;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import component.app.AppController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;

import java.util.ArrayList;
import java.util.List;

public class SheetController {

    private AppController appController;

    @FXML private ScrollPane borderPane;
    @FXML private GridPane gridPaneTop;
    @FXML private GridPane gridPaneLeft;
    @FXML private GridPane gridPaneSheet;
    @FXML public ScrollPane upDownScroller;
    @FXML public ScrollPane rightLeftScroller;
    public final StringProperty defaultCellStyle = new TextField().styleProperty();

    private final SimpleStringProperty dependsOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToDependsOnColor = new ArrayList<>();
    private final SimpleStringProperty influenceOnColor = new SimpleStringProperty("white");
    private final List<StringProperty> bindToInfluenceOnColor = new ArrayList<>();
    private final SimpleStringProperty colorProp = new SimpleStringProperty("white");
    private final List<StringProperty> bindToColorProp = new ArrayList<>();


    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth())));
        });

    }

    void setColor(SimpleStringProperty prop,String color) {
        prop.set("-fx-border-color: " + color + "; -fx-border-width: 2px;");
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    private void clearSheet() {
        gridPaneSheet.getChildren().clear();
        gridPaneTop.getChildren().clear();
        gridPaneLeft.getChildren().clear();
    }

    public void fillSheet(SheetDto sheet) {
        clearSheet();

        sheet.cells().forEach((coordinate, cell) -> {
            TextField cellField = new TextField(cell.effectiveValue().toString());
            cellField.setPrefWidth(100);
            cellField.setPrefHeight(30);
            cellField.setOnMouseClicked(event -> cellClicked(coordinate));
            cellField.setOnAction(event -> handleCellAction(cellField, coordinate));
            cellField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    removePaint();
                }
            });
            cellField.setId(coordinate.toString());
            gridPaneSheet.add(cellField, coordinate.col() , coordinate.row());
        });

        for (int row = 1; row <= sheet.numberOfRows(); row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setPrefWidth(50);
            rowLabel.setPrefHeight(30);
            rowLabel.setAlignment(Pos.CENTER);
            gridPaneLeft.add(rowLabel, 0, row);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        for (int col = 0; col <= sheet.numberOfColumns(); col++) {
            Label colLabel = new Label(Coordinate.getColumnLabel(col));
            colLabel.setPrefWidth(100);
            colLabel.setPrefHeight(30);
            gridPaneTop.add(colLabel, col, 0);
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }

    private void handleCellAction(TextField cellField, Coordinate coordinate) {

        try {
            appController.updateCell(coordinate, cellField.getText());
        } catch (LoopConnectionException e) {
            showError(e.getMessage());
        }

        fillSheet(appController.GetSheet());

    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void cellClicked(Coordinate coordinate){
        appController.cellClicked(coordinate);
    }

    public void Paint(List<Coordinate> coordinateList,String color,PropType propType){
        SimpleStringProperty styleProp = getProp(propType);
        List<StringProperty> bindingToProp = getBindingsToProp(propType);
        gridPaneSheet.getChildren().forEach(node -> {
            if(coordinateList.contains(CoordinateFactory.getCoordinate(node.getId()))){
                node.styleProperty().bind(styleProp);
                bindingToProp.add(node.styleProperty());
            }
        });

        setColor(styleProp,color);
    }


    public void removePaint(){
        dependsOnColor.set(defaultCellStyle.toString());
        influenceOnColor.set(defaultCellStyle.toString());
        colorProp.set(defaultCellStyle.toString());
        bindToColorProp.forEach(Property::unbind);
        bindToDependsOnColor.forEach(Property::unbind);
        bindToInfluenceOnColor.forEach(Property::unbind);
        bindToDependsOnColor.clear();
        bindToInfluenceOnColor.clear();
        bindToColorProp.clear();

    }

    private SimpleStringProperty getProp(PropType prop){
        return switch (prop) {
            case PropType.INFLUENCE_ON -> influenceOnColor;
            case PropType.DEPENDS_ON -> dependsOnColor;
            case PropType.COLOR -> colorProp;
        };
    }

    private List<StringProperty> getBindingsToProp(PropType prop){
        return switch (prop) {
            case PropType.INFLUENCE_ON -> bindToInfluenceOnColor;
            case PropType.DEPENDS_ON -> bindToDependsOnColor;
            case PropType.COLOR -> bindToColorProp;
        };

    }
}
