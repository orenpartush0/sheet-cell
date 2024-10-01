package sheetpage.top;

import javafx.event.ActionEvent;
import sheetpage.app.AppController;
import sheetpage.top.dialog.filter.FilterDialogController;
import sheetpage.top.dialog.function.FunctionDialogController;
import dashboard.dialog.sheet.SheetDialogController;
import sheetpage.top.dialog.range.RangeDialogController;
import sheetpage.top.dialog.sort.SortDialogController;
import dto.CellDto;
import dto.SheetDto;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticell.sheet.coordinate.CoordinateFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static constant.UIConstants.FALSE;
import static constant.UIConstants.NEED_TO_UPDATE_MESSAGE;

public class TopController {
    private AppController appController;

    private int counter = 0;


    @FXML
    private TextField userTextField;
    @FXML
    private Button updateButton;
    @FXML
    private TextField originalValueTextField;
    @FXML
    private TextField lastUpdateTextField;
    @FXML
    private TextField cellIdTextField;
    @FXML
    private ComboBox<String> SheetVersionComboBox;
    @FXML
    private ComboBox<String> rangesComboBox;
    @FXML
    private Button minus;
    @FXML
    private Button plus;
    @FXML
    private ColorPicker textColorPicker;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ComboBox<String> alignmentComboBox;
    @FXML
    private Button defaultStyleButton;
    @FXML
    private Button functionButton;
    @FXML
    public TextField fromTextField;
    @FXML
    public TextField toTextField;
    @FXML
    public TextField stepTextField;
    @FXML
    public Button dynamicButton;
    @FXML
    public Slider dynamicSlider;

    private final SimpleStringProperty userProperty = new SimpleStringProperty("");
    private final SimpleStringProperty originalValue = new SimpleStringProperty("");
    private final SimpleIntegerProperty lastUpdate = new SimpleIntegerProperty(0);
    private final SimpleStringProperty cellId = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        userTextField.textProperty().bind(userProperty);
        originalValueTextField.textProperty().bind(originalValue);
        lastUpdateTextField.textProperty().bind(Bindings.format("%d", lastUpdate));
        cellIdTextField.textProperty().bind(cellId);
        rangesComboBox.setVisibleRowCount(5);
        SheetVersionComboBox.setVisibleRowCount(5);
        SheetVersionComboBox.getItems().add("Version");
        SheetVersionComboBox.setOnAction(event -> {
            if(SheetVersionComboBox.getValue() != null){
                if (!SheetVersionComboBox.getValue().equals("Version")) {
                    try {handleVersionSelected(SheetVersionComboBox.getValue());
                    } catch (IOException e) {throw new RuntimeException(e);}
                    SheetVersionComboBox.setValue("Version");
                }
            }
        });
        dynamicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int step = Integer.parseInt(stepTextField.getText());
            double adjustedValue = Math.round(newValue.doubleValue() / step) * step;
            dynamicSlider.setValue(adjustedValue);
        });



        rangesComboBox.setOnAction(event -> {
            String selectedRange = rangesComboBox.getSelectionModel().getSelectedItem();
            appController.removePaint();
            handleRangeSelected(selectedRange);
            minus.setOnMouseClicked(mouseEvent -> {
                if(!appController.GetNeedToBeUpdated()) {
                    appController.removePaint();
                    try {
                        appController.removeRange(selectedRange);
                    } catch (Exception e) {
                        AppController.showError(e.getMessage());
                    }
                }else{
                    AppController.showError(NEED_TO_UPDATE_MESSAGE);
                }
            });
        });

        textColorPicker.setOnAction(event -> {
            Color selectedColor = textColorPicker.getValue();
            String colorAsHex = String.format("#%02X%02X%02X",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));

            appController.PaintCellText(CoordinateFactory.getCoordinate(cellId.get()), colorAsHex);
        });

        backgroundColorPicker.setOnAction(event -> {
            Color selectedColor = backgroundColorPicker.getValue();
            String colorAsHex = String.format("#%02X%02X%02X",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));

            appController.PaintCellBackground(CoordinateFactory.getCoordinate(cellId.get()), colorAsHex);
        });

        alignmentComboBox.setOnAction(event -> {
            String selectedAlignment = alignmentComboBox.getSelectionModel().getSelectedItem();
            Pos selectedPos = convertToPos(selectedAlignment);
            appController.setAlignment(CoordinateFactory.getCoordinate(cellId.getValue()).col(), selectedPos);
        });

        makeNumericOnly(toTextField);
        makeNumericOnly(fromTextField);
        makeNumericOnly(stepTextField);

        dynamicSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                appController.applyDynamicCalculate(CoordinateFactory.getCoordinate(cellId.getValue()), String.valueOf(newValue));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        fromTextField.setOnMouseClicked(e->{
            dynamicButton.managedProperty().set(true);
            dynamicSlider.managedProperty().set(false);
            dynamicButton.visibleProperty().set(true);
            dynamicSlider.visibleProperty().set(false);
        });
        toTextField.setOnMouseClicked(e->{
            dynamicButton.managedProperty().set(true);
            dynamicSlider.managedProperty().set(false);
            dynamicButton.visibleProperty().set(true);
            dynamicSlider.visibleProperty().set(false);
        });
        stepTextField.setOnMouseClicked(e->{
            dynamicButton.managedProperty().set(true);
            dynamicSlider.managedProperty().set(false);
            dynamicButton.visibleProperty().set(true);
            dynamicSlider.visibleProperty().set(false);
        });

    }

    public void setUpdateVisible(boolean bool){
        updateButton.visibleProperty().set(bool);
    }

    public void BindEditAble(SimpleBooleanProperty editAble){
        plus.visibleProperty().bind(editAble);
        minus.visibleProperty().bind(editAble);
    }

    private void makeNumericOnly(TextField textField) {
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    textField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        textField.setOnMouseClicked(e-> counter = 0);
    }

    private Pos convertToPos(String alignment) {
        return switch (alignment) {
            case "Left" -> Pos.CENTER_LEFT;
            case "Right" -> Pos.CENTER_RIGHT;
            default -> Pos.CENTER;
        };
    }


    public void setAppController(AppController appController) {
        this.appController = appController;
    }


    private Color extractColorFromStyle(String style, String colorProperty) {
        Pattern pattern = Pattern.compile(colorProperty + ":\\s*([^;]+)");
        Matcher matcher = pattern.matcher(style);
        if (matcher.find()) {
            String colorValue = matcher.group(1);
            return Color.web(colorValue);
        }
        return Color.TRANSPARENT;
    }

    public void setOnMouseCoordinate(CellDto cell, String style, Pos pos) {
        dynamicButton.visibleProperty().set(true);
        dynamicSlider.visibleProperty().set(false);
        dynamicSlider.managedProperty().set(false);
        dynamicButton.managedProperty().set(true);
        cellId.set(cell.coordinate().toString());
        originalValue.set(cell.originalValue());
        userProperty.set(cell.userName());
        lastUpdate.set(cell.LatestSheetVersionUpdated());
        textColorPicker.disableProperty().set(false);
        defaultStyleButton.disableProperty().set(false);
        functionButton.disableProperty().set(false);
        toTextField.disableProperty().set(false);
        fromTextField.disableProperty().set(false);
        stepTextField.disableProperty().set(false);
        backgroundColorPicker.disableProperty().set(false);
        alignmentComboBox.disableProperty().set(false);
        Color textColor = extractColorFromStyle(style, "-fx-text-fill");
        Color backgroundColor = extractColorFromStyle(style, "-fx-background-color");
        textColorPicker.setValue(textColor);
        backgroundColorPicker.setValue(backgroundColor);
        alignmentComboBox.setValue(convertFromPos(pos));
    }

    private String convertFromPos(Pos pos) {
        return switch (pos) {
            case CENTER_LEFT -> "Left";
            case CENTER_RIGHT -> "Right";
            default -> "Center";
        };
    }

    public void onSort() throws IOException {
        if(!appController.GetNeedToBeUpdated()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sheetpage/top/dialog/sort/createSortDialog.fxml"));
            Parent root = loader.load();
            SortDialogController controller = loader.getController();
            controller.setAppController(appController);
            controller.setBoundaries(appController.getNumOfCols(), appController.getNumOfRows());
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Sort");
            controller.setDialogStage(dialogStage);
            controller.setAppController(appController);
            dialogStage.showAndWait();
        }else {
            AppController.showError(NEED_TO_UPDATE_MESSAGE);
        }
    }


    @FXML
    public void onFilter() throws IOException {
        if(!appController.GetNeedToBeUpdated()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sheetpage/top/dialog/filter/createFilterDialog.fxml"));
            Parent root = loader.load();
            FilterDialogController controller = loader.getController();
            controller.setBoundaries(appController.getNumOfRows(), appController.getNumOfCols());
            controller.fillData();
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setTitle("Filter");
            controller.setDialogStage(dialogStage);
            controller.setAppController(appController);
            dialogStage.showAndWait();
        }else {
            AppController.showError(NEED_TO_UPDATE_MESSAGE);
        }
    }

    @FXML
    public void addRangeAction() throws IOException {
        if (!appController.GetNeedToBeUpdated()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sheetpage/top/dialog/range/createRangeDialog.fxml"));
            Parent root = loader.load();

            RangeDialogController controller = loader.getController();
            controller.setBoundaries(appController.getNumOfRows(), appController.getNumOfCols());
            controller.setAppController(appController);
            controller.setTopController(this);
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Range Details");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setHeight(240);
            dialogStage.setWidth(350);
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        }else{
            AppController.showError(NEED_TO_UPDATE_MESSAGE);
        }
    }

    private void handleRangeSelected(String selectedItem) {
        if(appController.ranges.get(selectedItem) != null){
            appController.PaintCells(appController.ranges.get(selectedItem).getRangeCellsCoordinate(),"pink");
        }
    }

    public void addRangesToComboBox(List<String> ranges) {
        clearRangeComboBox();
        ranges.forEach(this::addRangeToComboBox);

    }

    private void addRangeToComboBox(String rangeName) {
        rangesComboBox.getItems().add(rangeName);
    }

    private void clearRangeComboBox() {
        rangesComboBox.getItems().clear();
    }

    public void addVersions(int version) {
        SheetVersionComboBox.getItems().clear();
        SheetVersionComboBox.getItems().add("Version");
        for(int i = 1 ; i <= version ;i++){
            SheetVersionComboBox.getItems().add(String.valueOf(i));
        }
    }

    private void handleVersionSelected(String selectedItem) throws IOException {
        SheetDto sheetByVersionVersion = appController.getSheetByVersion(Integer.parseInt(selectedItem));
        appController.createNewSheetInDifferentWindows(sheetByVersionVersion);
    }

    @FXML
    public void onDefaultStyle() {
        appController.setDefaultStyle(CoordinateFactory.getCoordinate(cellId.get()));
    }

    @FXML
    public void onFunction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sheetpage/top/dialog/function/createFunctionDialog.fxml"));
        Parent root = loader.load();
        SimpleStringProperty function = new SimpleStringProperty();
        FunctionDialogController controller = loader.getController();
        controller.setFunction(function);
        Stage dialogStage = new Stage();
        controller.setDialogStage(dialogStage);
        dialogStage.setTitle("Function Details");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setHeight(240);
        dialogStage.setWidth(450);
        dialogStage.showAndWait();
        if (controller.isClick) {
            appController.createFunc(function, CoordinateFactory.getCoordinate(cellId.get()));
        }
    }


    @FXML
    public void showSlider() throws IOException {
        if (!fromTextField.getText().isEmpty() && !toTextField.getText().isEmpty() && !stepTextField.getText().isEmpty()) {
            int from = Integer.parseInt(fromTextField.getText());
            int to = Integer.parseInt(toTextField.getText());
            int step = Integer.parseInt(stepTextField.getText());

            if (from > to || to - from < step) {
                AppController.showError("Invalid arguments");
            } else {
                dynamicSlider.setMin(from);  // Set min first
                dynamicSlider.setMax(to);    // Set max after min
                dynamicSlider.setValue(from);  // Set value last, after min and max are set

                dynamicSlider.setBlockIncrement(step); // Set block increment to the step size
                dynamicSlider.setMajorTickUnit(step);
                dynamicButton.managedProperty().set(false);
                dynamicSlider.visibleProperty().set(true);
                dynamicSlider.managedProperty().set(true);
                dynamicButton.visibleProperty().set(false);
            }
        }
    }


    @FXML
    public void OnBackHandler() {
        appController.OnBackHandler();
        appController.close();
    }

    public void UpdateSheetHandler() throws IOException {
        appController.fillSheet();
    }
}


