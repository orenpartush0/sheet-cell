package component.app;

import component.sheet.Enum.PropType;
import connector.Connector;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import component.sheet.SheetController;
import component.top.TopController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.range.Range;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AppController {

    public BorderPane root;
    private final Connector connector = new Connector();

    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;

    private int numOfCols;
    private int numOfRows;

    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
    }

    public void importFile(String path) {
        try {
            connector.SetSheet(path);
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
        SheetDto sheet = connector.getSheet();
        numOfCols = sheet.numberOfColumns();
        numOfRows = sheet.numberOfRows();
        sheetComponentController.fillSheet(sheet);
        topComponentController.addRangesToComboBox(connector.getRanges().stream().map(Range::rangeName).toList());
    }

    public void saveSheet(String path) throws IOException {
        connector.InsertSheetToBinaryFile(path);
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        connector.SetSheet(new SheetDto(sheetName,1,numColumns,numRows,100,30));
        sheetComponentController.clearSheet();
        fillSheet();
        topComponentController.clearVersion();
        topComponentController.addVersion();
    }

    public void updateCell(Coordinate coordinate,String value) throws LoopConnectionException {
        topComponentController.addVersion();
        connector.UpdateCellByCoordinate(coordinate,value);
    }

    public SheetDto GetSheet(){
        return connector.getSheet();
    }

    public void cellClicked(Coordinate coordinate){
        topComponentController.setOnMouseCoordinate(connector.GetCellByCoordinate(coordinate));
        List<Coordinate> influenceOn = connector.getSheet().cells().get(coordinate).influenceOn();
        List<Coordinate> dependsOn = connector.getSheet().cells().get(coordinate).dependsOn();
        sheetComponentController.Paint(influenceOn,"Green", PropType.INFLUENCE_ON);
        sheetComponentController.Paint(dependsOn,"Blue",PropType.DEPENDS_ON);
    }

    public void PaintCells(List<Coordinate> coordinates,String color){
        sheetComponentController.Paint(coordinates,color,PropType.COLOR);
    }

    public void addRange(String rangeName,Coordinate startCoordinate,Coordinate endCoordinate){
        connector.AddRange(new Range(rangeName,startCoordinate,endCoordinate));
        fillSheet();
    }

    public Range GetRange(String rangeName) {
        return connector.GetRangeDto(rangeName);
    }

    public void removePaint(){
        sheetComponentController.removePaint();
    }

    public SheetDto getSheetByVersion(int version){
        return connector.GetSheetByVersion(version);
    }

    public int getNumOfCols(){
        return numOfCols;
    }

    public int getNumOfRows(){
        return numOfRows;
    }

    public Map<Integer, List<String>> getValuesInColumns(Range range ){
        return connector.getValuesInColumn(range);
    }
    public SheetDto applyFilter(Range range ,Map<Integer, List<String>> filters) {
        return connector.applyFilter(range,filters);
    }

    public void removeRange(String rangeName){
        connector.removeRange(rangeName);
    }
}
