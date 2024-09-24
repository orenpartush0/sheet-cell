package sheetpage.app;

import Connector.Connector;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import sheetpage.sheet.SheetController;
import sheetpage.top.TopController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class AppController {

    public BorderPane root;
    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;

    public SheetDto sheet;

    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
    }

    public void importFile(String path) {
        try {
            Connector.SetSheet(path);
            topComponentController.clearVersion();
            topComponentController.addVersion();
            sheetComponentController.clearSheet();
            fillSheet();
            topComponentController.EnableButtons();
        }
        catch (Exception e) {
            topComponentController.setPreviousPath();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while importing the file.");
            alert.setContentText(e.getMessage());
            Platform.runLater(alert::showAndWait);
        }
    }

    private void fillSheet(){
        sheet = Connector.getSheet();
        sheetComponentController.fillSheet(sheet);
        topComponentController.addRangesToComboBox(Connector.getRanges().stream().map(Range::rangeName).toList());
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        //Connector.SetSheet(new SheetDto(sheetName,1,numColumns,numRows,100,30));
        sheetComponentController.clearSheet();
        fillSheet();
        topComponentController.clearVersion();
        topComponentController.addVersion();
    }

    public void updateCell(Coordinate coordinate, String value) {
        try {
            Connector.UpdateCellByCoordinate(coordinate, value);
            topComponentController.addVersion();
        }
        catch (Exception e){
            showError(e.getMessage());
        }
    }

    public SheetDto GetSheet(){
        return Connector.getSheet();
    }

    public void cellClicked(Coordinate coordinate,String style,Pos pos){
        topComponentController.setOnMouseCoordinate(Connector.GetCellByCoordinate(coordinate),style,pos);
        List<Coordinate> influenceOn = Connector.getSheet().cells().get(coordinate).influenceOn();
        List<Coordinate> dependsOn = Connector.getSheet().cells().get(coordinate).dependsOn();
        sheetComponentController.PaintCellsBorder(influenceOn,"Green");
        sheetComponentController.PaintCellsBorder(dependsOn,"Blue");
    }

    public void PaintCells(List<Coordinate> coordinates,String color){
        sheetComponentController.PaintCellsBorder(coordinates,color);
    }

    public void addRange(String rangeName,Coordinate startCoordinate,Coordinate endCoordinate){
        Connector.AddRange(new Range(rangeName,startCoordinate,endCoordinate));
        fillSheet();
    }

    public Range GetRange(String rangeName) {
        return Connector.GetRangeDto(rangeName);
    }

    public void removePaint(){
        sheetComponentController.removeBorderPaint();
    }

    public SheetDto getSheetByVersion(int version){
        return Connector.GetSheetByVersion(version);
    }

    public int getNumOfCols(){
        return sheet.numberOfColumns();
    }

    public int getNumOfRows(){
        return sheet.numberOfRows();
    }

    public Map<Integer, List<String>> getValuesInColumns(Range range ){
        return Connector.getValuesInColumn(range);
    }
    public SheetDto applyFilter(Range range ,Map<Integer, List<String>> filters) {
        return Connector.applyFilter(range,filters);
    }

    public void removeRange(String rangeName) throws Exception{
        Connector.removeRange(rangeName);
    }

    public static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void PaintCellText(Coordinate coordinate, String color){
        sheetComponentController.PaintCellText(coordinate,color);
    }

    public void PaintCellBackground(Coordinate coordinate, String color){
        sheetComponentController.PaintCellBackground(coordinate,color);
    }

    public void setAlignment(int col, Pos pos){
        sheetComponentController.setAlignment(col,pos);
    }

    public void createNewSheetInDifferentWindows(SheetDto sheet){
        sheetComponentController.createNewSheetInDifferentWindows(sheet);
    }

    public SheetDto applySort(Queue<String> cols,Range range){
        return Connector.applySort(cols,range);
    }

    public void setDefaultStyle(Coordinate coordinate){
        sheetComponentController.setDefaultStyle(coordinate);
    }

    public void createFunc(SimpleStringProperty func, Coordinate coordinate){
        sheetComponentController.createFunc(func,coordinate);
    }

    public void applyDynamicCalculate(Coordinate coordinate, String numStr){
        SheetDto sheetDto = Connector.applyDynamicCalculate(coordinate, numStr);
        sheetComponentController.fillSheet(sheetDto);
    }
}
