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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import component.app.AppController;
import javafx.stage.Stage;
import javafx.util.Duration;
import shticell.sheet.coordinate.Coordinate;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.IntStream;

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
    private final Map<Coordinate,TextField> sheetTextFields = new HashMap<>();
    private final Map<Integer, ObjectProperty<Pos>> alignmentPerCol = new HashMap<>();

    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth()))));
    }

    private double xOffset;
    private double yOffset;
    private double initialWidth;
    private double initialHeight;

    private final Map<Integer, SimpleIntegerProperty> colWidth = new HashMap<>();
    private final Map<Integer, SimpleIntegerProperty> rowHeight = new HashMap<>();


    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    public void clearSheet() {
        isSheetLoaded = false;
        gridPaneSheet.getChildren().clear();
        gridPaneTop.getChildren().clear();
        gridPaneLeft.getChildren().clear();
    }
    public void printRowAndColumnsLabels(SheetDto sheet, GridPane gridPaneLeft, GridPane gridPaneTop) {
        for (int row = 1; row <= sheet.numberOfRows(); row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.prefWidthProperty().set(30);
            rowLabel.prefHeightProperty().bind(rowHeight.get(row));
            rowLabel.setAlignment(Pos.CENTER);
            gridPaneLeft.add(rowLabel, 0, row);
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        Label leftTopTextField = new Label("");
        leftTopTextField.prefWidthProperty().set(30);
        gridPaneTop.add(leftTopTextField, 0, 0);

        for (int col = 1; col <= sheet.numberOfColumns(); col++) {
            Label colLabel = new Label(Coordinate.getColumnLabel(col));
            colWidth.computeIfPresent(col, (key, value) -> {
                colLabel.prefWidthProperty().bind(value);
                return value;
            });
            colLabel.setPrefHeight(30);
            colLabel.setAlignment(Pos.CENTER);
            gridPaneTop.add(colLabel, col, 0);
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }

    public void fillSheet(SheetDto sheet) {
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
            colWidth.put(i,new SimpleIntegerProperty(100));
        }

        for (int i = 1; i <= sheet.numberOfRows(); i++) {
            rowHeight.put(i,new SimpleIntegerProperty(30));
        }

        sheet.cells().forEach((coordinate, cell) -> {
            TextField cellField = new TextField(cell.effectiveValue().toString());
            cellField.prefWidthProperty().bindBidirectional(colWidth.get(coordinate.col()));
            cellField.prefHeightProperty().bindBidirectional(rowHeight.get(coordinate.row()));

            cellField.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
                initialWidth = cellField.getWidth();
                initialHeight = cellField.getHeight();
            });

            cellField.setOnMouseDragged(event -> {
                double newWidth = initialWidth + (event.getSceneX() - xOffset);
                cellField.setPrefWidth(newWidth > 50 ? newWidth : 50);

                double newHeight = initialHeight + (event.getSceneY() - yOffset);
                cellField.setPrefHeight(newHeight > 20 ? newHeight : 20);
            });


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

        printRowAndColumnsLabels(sheet, gridPaneLeft, gridPaneTop);
    }

    private void handleCellAction(TextField cellField, Coordinate coordinate) {
        appController.updateCell(coordinate, cellField.getText());
        fillSheet(appController.GetSheet());
    }

    private void cellClicked(Coordinate coordinate) {
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

    public void createNewSheetInDifferentWindows(SheetDto sheet) {
        GridPane tempSheetGridPane = new GridPane();
        GridPane tempLeftGridPane = new GridPane();
        GridPane tempTopGridPane = new GridPane();
        BorderPane borderPane = new BorderPane();

        sheet.cells().forEach((coordinate, cell) -> {
            TextField cellField = new TextField(cell.effectiveValue().toString());
            cellField.styleProperty().bind(sheetTextFields.get(coordinate).styleProperty());
            cellField.alignmentProperty().bind(sheetTextFields.get(coordinate).alignmentProperty());
            cellField.prefHeightProperty().bind(sheetTextFields.get(coordinate).prefHeightProperty());
            cellField.prefWidthProperty().bind(sheetTextFields.get(coordinate).prefWidthProperty());
            tempSheetGridPane.add(cellField, coordinate.col(), coordinate.row());
        });

        IntStream.range(0,sheet.numberOfRows()).forEach(i->{
            Label currentRowLabel = ((Label)gridPaneLeft.getChildren().get(i));
            Label rowLabel = new Label(currentRowLabel.getText());
            rowLabel.setStyle(currentRowLabel.getStyle());
            rowLabel.setAlignment(currentRowLabel.getAlignment());
            rowLabel.setPrefHeight(currentRowLabel.getPrefHeight());
            rowLabel.setPrefWidth(currentRowLabel.getPrefWidth());
            tempLeftGridPane.add(rowLabel,0,i);
        });

        IntStream.range(0,sheet.numberOfColumns() + 1).forEach(i -> {
            Label currentColLabel = ((Label)gridPaneTop.getChildren().get(i));
            Label colLabel = new Label(currentColLabel.getText());
            colLabel.setStyle(currentColLabel.getStyle());
            colLabel.setAlignment(currentColLabel.getAlignment());
            colLabel.setPrefHeight(currentColLabel.getPrefHeight());
            colLabel.setPrefWidth(currentColLabel.getPrefWidth());
            tempTopGridPane.add(colLabel,i,0);
        });

        borderPane.setCenter(tempSheetGridPane);
        borderPane.setLeft(tempLeftGridPane);
        borderPane.setTop(tempTopGridPane);

        Stage newStage = new Stage();

        Scene scene = new Scene(borderPane, 800, 600);
        newStage.setScene(scene);
        newStage.show();
    }

    public void setDefaultStyle(Coordinate coordinate){
        sheetTextFields.get(coordinate).styleProperty().setValue(defaultCellStyle);
    }

}

