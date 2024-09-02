package scene.sheet;

import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import scene.app.AppController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.coordinate.CoordinateFactory;
import shticell.sheet.exception.LoopConnectionException;

import java.util.List;

public class SheetController {

    private AppController appController;

    @FXML
    private ScrollPane borderPane;

    @FXML
    private GridPane gridPaneTop;

    @FXML
    private GridPane gridPaneLeft;

    @FXML
    private GridPane gridPaneSheet;

    @FXML
    public ScrollPane upDownScroller;

    @FXML
    public ScrollPane rightLeftScroller;


    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth())));
        });
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
            cellField.setOnMouseReleased(event -> cellUnClicked(coordinate));
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

    private void cellUnClicked(Coordinate coordinate){
        gridPaneSheet.getChildren().forEach(node->node.setStyle("-fx-border-color: white; -fx-border-width: 2px;"));
    }

    public void PaintCells(List<Coordinate> influenceOn,List<Coordinate>  dependsOn){
        gridPaneSheet.getChildren().forEach(node -> {
            if(influenceOn.contains(CoordinateFactory.getCoordinate(node.getId()))){
                node.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
            }
            else if(dependsOn.contains(CoordinateFactory.getCoordinate(node.getId()))){
                node.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");
            }
        });
    }
}
