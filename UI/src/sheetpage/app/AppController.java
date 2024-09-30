package sheetpage.app;

import apiConnector.Connector;
import constant.Constants;
import dashboard.DashBoardController;
import dashboard.ListRefresher;
import dto.SheetDto;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import sheetpage.sheet.SheetController;
import sheetpage.top.TopController;
import shticell.manager.enums.PermissionType;
import shticell.sheet.coordinate.Coordinate;
import shticell.sheet.range.Range;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;


public class AppController implements Closeable {

    public BorderPane root;

    @FXML private TopController topComponentController;
    @FXML private SheetController sheetComponentController;

    private boolean isNeedToBeUpdated = false;
    public Map<String,Range> ranges = new HashMap<>();
    public SheetDto sheet;
    public Stage sheetStage;
    public Stage dashBoardStage;
    private String sheetName;

    private final Timer timer = new Timer();
    private final SimpleBooleanProperty editable = new SimpleBooleanProperty(true);


    @FXML
    public void initialize() {
        topComponentController.setAppController(this);
        sheetComponentController.setAppController(this);
        topComponentController.BindEditAble(editable);
    }

    public boolean GetNeedToBeUpdated(){
        return isNeedToBeUpdated;
    }

    public void SetNeedToBeUpdated(boolean isNeed){
        isNeedToBeUpdated = isNeed;
    }

    private void setSheetListRefresher(){
        NeedToBeUpdatedRefresher needToBeUpdatedRefresher =
                new NeedToBeUpdatedRefresher(this::SetNeedToBeUpdated,sheetName);

        timer.schedule(needToBeUpdatedRefresher,0,1000);
    }


    public void fillSheet() throws IOException {
        sheet = Connector.getSheet(sheetName);
        sheetComponentController.fillSheet(sheet);
        sheetComponentController.BindEditAble(editable);
        topComponentController.addVersions(sheet.version());
        Connector.getRanges(sheet.Name(), new Connector.RangesCallback(){
            @Override
            public void onSuccess(List<Range> rangesFromServer) {
                List<String> rangeNames = rangesFromServer.stream()
                        .map(Range::rangeName)
                        .toList();
                topComponentController.addRangesToComboBox(rangeNames);
                ranges.clear();
                rangesFromServer.forEach(range->ranges.put(range.rangeName(), range));
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }



    public void updateCell(Coordinate coordinate, String value) {
        try {
            Connector.UpdateCellByCoordinate(sheetName,coordinate, value);
            fillSheet();
        }
        catch (Exception e){
            showError(e.getMessage());
        }
    }

    public SheetDto GetSheet(){
        return sheet;
    }

    public void cellClicked(Coordinate coordinate,String style,Pos pos){
        topComponentController.setOnMouseCoordinate(sheet.cells().get(coordinate),style,pos);
        List<Coordinate> influenceOn = sheet.cells().get(coordinate).influenceOn();
        List<Coordinate> dependsOn = sheet.cells().get(coordinate).dependsOn();
        sheetComponentController.PaintCellsBorder(influenceOn,"Green");
        sheetComponentController.PaintCellsBorder(dependsOn,"Blue");
    }

    public void PaintCells(List<Coordinate> coordinates,String color){
        sheetComponentController.PaintCellsBorder(coordinates,color);
    }

    public void addRange(String rangeName,Coordinate startCoordinate,Coordinate endCoordinate){
        Connector.AddRange(new Range(rangeName,startCoordinate,endCoordinate),sheetName, new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Platform.runLater(() -> {
                        ranges.put(rangeName, new Range(rangeName, startCoordinate, endCoordinate));
                        List<String> rangeNames = ranges
                                .values()
                                .stream()
                                .map(Range::rangeName)
                                .toList();


                        topComponentController.addRangesToComboBox(rangeNames);
                    });
                }else{
                    Platform.runLater(()-> {
                        try {
                            showError(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}
        });
    }

    public void removePaint(){
        sheetComponentController.removeBorderPaint();
    }

    public SheetDto getSheetByVersion(int version) throws IOException {
        return Connector.GetSheetByVersion(version,sheetName);
    }

    public int getNumOfCols(){
        return sheet.numberOfColumns();
    }

    public int getNumOfRows(){
        return sheet.numberOfRows();
    }

    public SheetDto applyFilter(Range range ,Map<Integer, List<String>> filters) throws IOException {
        return Connector.applyFilter(sheetName,range,filters);
    }

    public void removeRange(String rangeName){
        Connector.removeRange(rangeName, sheetName, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {}

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response){
                    Platform.runLater(() -> {
                        ranges.remove(rangeName);
                        List<String> rangeNames = ranges
                                .values()
                                .stream()
                                .map(Range::rangeName)
                                .toList();


                        topComponentController.addRangesToComboBox(rangeNames);
                    });
                    if(!response.isSuccessful()){
                    Platform.runLater(()-> {
                        try {
                            showError(response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
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

    public SheetDto applySort(Queue<String> cols,Range range) throws IOException {
        return Connector.applySort(cols,range,sheetName);
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

    public void SetStages(Stage sheetStage,Stage dashBoardStage) {
        this.sheetStage = sheetStage;
        this.dashBoardStage = dashBoardStage;
    }

    public void SetPermission(PermissionType permissionType) {
        editable.set(permissionType.getPermissionLevel() >= PermissionType.WRITER.getPermissionLevel());
    }

    public void SetSheetName(String sheetName) {
        this.sheetName = sheetName;
        setSheetListRefresher();
    }

    public void OnBackHandler(){
        sheetStage.close();
        dashBoardStage.show();
    }

    @Override
    public void close() {
        timer.cancel();
    }
}
