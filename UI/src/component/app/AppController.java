package component.app;

import connector.Connector;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import component.sheet.SheetController;
import component.top.TopController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;
import shticell.sheet.range.Range;

import java.io.IOException;
import java.util.List;

public class AppController {

    public BorderPane root;
    private final Connector connector = new Connector();

    @FXML private VBox rangeComponent;
    @FXML private VBox topComponent;
    @FXML private ScrollPane sheetComponent;
    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;


    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
    }

    public void importFile(String path) {
        try {
            connector.SetSheet(path);
            fillSheet();
            topComponentController.setSaveButtonAble();
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
        sheetComponentController.fillSheet(connector.getSheet());
        topComponentController.addRangesToComboBox(connector.getRanges().stream().map(Range::rangeName).toList());
    }

    public void saveSheet(String path) throws IOException {
        connector.InsertSheetToBinaryFile(path);
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        connector.SetSheet(new SheetDto(sheetName,1,numColumns,numRows,1,1));
        fillSheet();
        topComponentController.setSaveButtonAble();
    }

    public void updateCell(Coordinate coordinate,String value) throws LoopConnectionException {
        connector.UpdateCellByCoordinate(coordinate,value);
    }

    public SheetDto GetSheet(){
        return connector.getSheet();
    }

    public void cellClicked(Coordinate coordinate){
        topComponentController.setOnMouseCoordinate(connector.GetCellByCoordinate(coordinate));
        List<Coordinate> influenceOn = connector.getSheet().cells().get(coordinate).influenceOn();
        List<Coordinate> dependsOn = connector.getSheet().cells().get(coordinate).dependsOn();
        sheetComponentController.PaintCells(influenceOn,"Green");
        sheetComponentController.PaintCells(dependsOn,"Blue");
    }

    public void PaintCells(List<Coordinate> coordinates,String color){
        sheetComponentController.PaintCells(coordinates,color);
    }

    public void addRange(String rangeName,Coordinate startCoordinate,Coordinate endCoordinate){
        connector.AddRange(new Range(rangeName,startCoordinate,endCoordinate));
    }


    public Range GetRangeDto(String rangeName){
        return connector.GetRangeDto(rangeName);
    }

    public Range GetRange(String rangeName) {
        return connector.GetRangeDto(rangeName);
    }

    public List<Range> getRanges(){
        return connector.getRanges();
    }

    public void removePaint(){
        sheetComponentController.removePaint();
    }

}
