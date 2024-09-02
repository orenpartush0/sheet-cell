package scene.app;

import connector.Connector;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scene.sheet.SheetController;
import scene.top.TopController;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.exception.LoopConnectionException;

import java.io.IOException;
import java.util.List;

public class AppController {

    private Connector connector;

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
            connector = new Connector(path);
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
    }

    public void saveSheet(String path) throws IOException {
        connector.InsertSheetToBinaryFile(path);
    }

    public void createNewSheet(String sheetName, int numColumns, int numRows){
        connector = new Connector(new SheetDto(sheetName,1,numColumns,numRows,1,1));
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

}
