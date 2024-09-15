package component.sheet;

import dto.SheetDto;
import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import component.app.AppController;
import javafx.util.Duration;
import shticell.sheet.coordinate.Coordinate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class SheetController {

    private AppController appController;
    private boolean isSheetLoaded = false;

    @FXML private GridPane gridPaneTop;
    @FXML private GridPane gridPaneLeft;
    @FXML private GridPane gridPaneSheet;
    @FXML public ScrollPane upDownScroller;
    @FXML public ScrollPane rightLeftScroller;

    private final String defaultCellStyle = new TextField().styleProperty().toString();
    private final Map<Coordinate,SimpleStringProperty> sheetData = new HashMap<>();
    private final SimpleIntegerProperty cellWidth = new SimpleIntegerProperty(100);
    private final SimpleIntegerProperty cellHeight = new SimpleIntegerProperty(30);
    private final Map<Coordinate,TextField> sheetTextFields = new HashMap<>();
    private final Map<Integer, ObjectProperty<Pos>> alignmentPerCol = new HashMap<>();

    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth()))));
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void clearSheet() {
        isSheetLoaded = false;
        gridPaneSheet.getChildren().clear();
        gridPaneTop.getChildren().clear();
        gridPaneLeft.getChildren().clear();
    }
    public static void printRowAndColumnsLabels(SheetDto sheet, GridPane gridPaneLeft, GridPane gridPaneTop,int width, int height) {
        for (int row = 1; row <= sheet.numberOfRows(); row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setPrefWidth((double) width /2);
            rowLabel.setPrefHeight(height);
            rowLabel.setAlignment(Pos.CENTER);
            gridPaneLeft.add(rowLabel, 0, row);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        for (int col = 0; col <= sheet.numberOfColumns(); col++) {
            Label colLabel = new Label(Coordinate.getColumnLabel(col));
            colLabel.setPrefWidth(width);
            colLabel.setPrefHeight(height);
            gridPaneTop.add(colLabel, col, 0);
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }

    public void fillSheet(SheetDto sheet) {
        cellWidth.set(sheet.colsWidth());
        cellHeight.set(sheet.rowsHeight());
        if (isSheetLoaded) {
            fillExistSheetWithData(sheet);
        } else {
            createNewSheet(sheet);
            isSheetLoaded = true;
        }
    }

    private String configStr(String str) {
        if (str.equalsIgnoreCase("true")) {
            return "True";
        } else if (str.equalsIgnoreCase("false")) {
            return "False";
        }

        try {
            long number = Long.parseLong(str);
            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
            df.applyPattern("#,###");
            return df.format(number);
        } catch (NumberFormatException e) {
            return str;
        }
    }

    private void fillExistSheetWithData(SheetDto sheet){
        sheet.cells().forEach((coordinate, cellDto) -> sheetData.get(coordinate).set(""));
        PauseTransition pause = new PauseTransition(Duration.millis(100));
        pause.setOnFinished(event -> sheet.cells().forEach((coordinate, cellDto) -> {
            String str = cellDto.effectiveValue().toString();
            sheetData.get(coordinate).set(configStr(str));}
        ));
        pause.play();
    }

    private void createNewSheet(SheetDto sheet){

        for (int i = 1; i <= sheet.numberOfColumns(); i++) {
            alignmentPerCol.put(i, new SimpleObjectProperty<>(Pos.CENTER_LEFT));
        }

        sheet.cells().forEach((coordinate, cell) -> {
            TextField cellField = new TextField(cell.effectiveValue().toString());
            cellField.prefWidthProperty().bind(cellWidth);
            cellField.prefHeightProperty().bind(cellHeight);
            sheetData.put(coordinate,new SimpleStringProperty(cell.effectiveValue().toString()));
            sheetTextFields.put(coordinate,cellField);
            cellField.alignmentProperty().bind(alignmentPerCol.get(coordinate.col()));
            cellField.textProperty().bindBidirectional(sheetData.get(coordinate));
            cellField.setOnMouseClicked(event -> cellClicked(coordinate));
            cellField.setOnAction(event -> handleCellAction(cellField, coordinate));
            cellField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    removeBorderPaint();
                }
            });
            cellField.setId(coordinate.toString());
            gridPaneSheet.add(cellField, coordinate.col() , coordinate.row());
        });

        printRowAndColumnsLabels(sheet, gridPaneLeft, gridPaneTop,cellWidth.get(),cellHeight.get());
    }

    private void handleCellAction(TextField cellField, Coordinate coordinate) {

        appController.updateCell(coordinate, cellField.getText());
        fillSheet(appController.GetSheet());
    }

    private void cellClicked(Coordinate coordinate){
        appController.cellClicked(coordinate,sheetTextFields.get(coordinate).getStyle(),alignmentPerCol.get(coordinate.col()).getValue());
    }

    public void PaintCellsBorder(List<Coordinate> coordinateList,String color){
        sheetTextFields.
                entrySet().
                stream().
                filter(entry->coordinateList.contains(entry.getKey())).
                forEach(entry-> PaintCellBorder(entry.getKey(),color));
    }


    public void removeBorderPaint() {
        sheetTextFields.forEach((key, textField) -> {
            String currentStyle = textField.getStyle();

            String newStyle = currentStyle.replaceAll("-fx-border-color:\\s*[^;]+;", "").trim();

            textField.setStyle(newStyle);
        });
    }

    public void PaintCellText(Coordinate coordinate, String color) {
        TextField textField = sheetTextFields.get(coordinate);

        String currentStyle = textField.getStyle();

        String newStyle = currentStyle.replaceAll("-fx-text-fill:\\s*[^;]+;", "").trim();

        newStyle += "-fx-text-fill: " + color + ";";

        textField.setStyle(newStyle);
    }



    public void PaintCellBackground(Coordinate coordinate, String color) {
        TextField textField = sheetTextFields.get(coordinate);

        String currentStyle = textField.getStyle();

        String newStyle = currentStyle.replaceAll("-fx-background-color:\\s*[^;]+;", "").trim();

        newStyle += "-fx-background-color: " + color + ";";

        textField.setStyle(newStyle);
    }



    public void PaintCellBorder(Coordinate coordinate, String color) {
        TextField textField = sheetTextFields.get(coordinate);

        String currentStyle = textField.getStyle();

        String newStyle = currentStyle.replaceAll("-fx-border-color:\\s*[^;]+;", "").trim();

        newStyle += "; -fx-border-color: " + color + ";";

        textField.setStyle(newStyle);
    }

    public void setAlignment(int col, Pos pos){
        alignmentPerCol.get(col).set(pos);
    }

}

