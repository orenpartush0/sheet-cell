package sheetpage.app;

import Connector.Connector;
import dto.SheetDto;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.Response;
import sheetpage.sheet.SheetController;
import sheetpage.top.TopController;
import shticell.manager.enums.PermissionType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;
import util.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static constant.Constants.*;

public class AppController {

    public BorderPane root;

    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;

    public SheetDto sheet;
    public Stage dashboardStage;
    public Stage sheetStage;
    private PermissionType permissionType;
    private String sheetName;

    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
    }


    private void fillSheet() throws IOException {
        Response res = HttpClientUtil.runSync(
                SHEET + "?" + SHEET_NAME + "=" + sheetName,
                GET,
                null
        );

        System.out.println(res.body().string());
        sheet = GSON.fromJson(res.body().string(), SheetDto.class);
        System.out.println(sheet.cells());
        sheetComponentController.fillSheet(sheet);
        topComponentController.addRangesToComboBox(Connector.getRanges().stream().map(Range::rangeName).toList());

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
        topComponentController.setOnMouseCoordinate(Connector.GetCellByCoordinate(coordinate),style,pos);//need to improve
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

    public void SetStages(Stage dashboardStage, Stage sheetStage) {
        this.dashboardStage = dashboardStage;
        this.sheetStage = sheetStage;
    }

    public void SetPermission(PermissionType permissionType) {
        this.permissionType = permissionType;
    }

    public void SetSheetName(String sheetName) throws IOException {
        this.sheetName = sheetName;
        fillSheet();
    }

}
