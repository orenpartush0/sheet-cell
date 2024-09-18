package component.top;

import component.app.AppController;
import component.top.dialog.filter.FilterDialogController;
import component.top.dialog.function.FunctionDialogController;
import component.top.dialog.sheet.SheetDialogController;
import component.top.dialog.range.RangeDialogController;
import component.top.dialog.sort.SortDialogController;
import dto.CellDto;
import dto.SheetDto;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shticell.sheet.coordinate.CoordinateFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TopController {
    private AppController appController;

    private String previousPath;
    private int counter = 0;

    @FXML
    public MenuItem saveButton;
    @FXML
    private TextField pathTextField;
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
    private Button plus;
    @FXML
    private Button minus;
    @FXML
    private Button filterButton;
    @FXML
    private Button sortButton;
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
    public Button incrementButton;


    private final SimpleBooleanProperty isSheetLoaded = new SimpleBooleanProperty(false);
    private final SimpleStringProperty originalValue = new SimpleStringProperty("");
    private final SimpleIntegerProperty lastUpdate = new SimpleIntegerProperty(0);
    private final SimpleStringProperty cellId = new SimpleStringProperty("");
    private final SimpleStringProperty path = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        pathTextField.textProperty().bind(path);
        originalValueTextField.textProperty().bind(originalValue);
        lastUpdateTextField.textProperty().bind(Bindings.format("%d", lastUpdate));
        cellIdTextField.textProperty().bind(cellId);
        saveButton.disableProperty().bind(isSheetLoaded.not());
        SheetVersionComboBox.disableProperty().bind(isSheetLoaded.not());
        rangesComboBox.disableProperty().bind(isSheetLoaded.not());
        rangesComboBox.setVisibleRowCount(5);
        SheetVersionComboBox.setVisibleRowCount(5);
        plus.disableProperty().bind(isSheetLoaded.not());
        minus.disableProperty().bind(isSheetLoaded.not());
        filterButton.disableProperty().bind(isSheetLoaded.not());
        sortButton.disableProperty().bind(isSheetLoaded.not());
        pathTextField.styleProperty().unbind();
        SheetVersionComboBox.getItems().add("Version");
        SheetVersionComboBox.setOnAction(event -> {
            if (!SheetVersionComboBox.getValue().equals("Version")) {
                handleVersionSelected(SheetVersionComboBox.getValue());
                SheetVersionComboBox.setValue("Version");
            }
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

    @FXML
    private void handleLoadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML or BIN Files", "*.xml", "*.bin"));
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            previousPath = path.get();
            path.set(filePath);
            appController.importFile(filePath);
        }
    }

    @FXML
    void HandleSaveFile() throws IOException {
        if (path.get().endsWith(".xml") || path.get().isEmpty()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                TextInputDialog filenameDialog = new TextInputDialog();
                filenameDialog.setTitle("Enter Filename");
                filenameDialog.setHeaderText("Enter the name of the file:");
                filenameDialog.setContentText("Filename:");
                Optional<String> result = filenameDialog.showAndWait();
                if (result.isPresent()) {
                    String filename = result.get();
                    if (!filename.isEmpty()) {
                        filename = filename + ".bin";
                        File fileToSave = new File(selectedDirectory, filename);
                        appController.saveSheet(fileToSave.getAbsolutePath());
                    }
                }
            }
        } else {
            File file = new File(path.get());
            FileWriter writer = new FileWriter(file);
            appController.saveSheet(path.get());
        }

    }

    @FXML
    public void handleCreateNewSheet() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/sheet/createSheetDialog.fxml"));
        Parent root = loader.load();

        SheetDialogController controller = loader.getController();
        controller.setTopController(this);
        controller.setAppController(appController);
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Sheet Details");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));

        dialogStage.setHeight(240);

        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }

    public void EnableButtons() {
        isSheetLoaded.set(true);
    }

    public void setPreviousPath() {
        path.set(previousPath);
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
        cellId.set(cell.coordinate().toString());
        originalValue.set(cell.originalValue());
        lastUpdate.set(cell.LatestSheetVersionUpdated());
        textColorPicker.disableProperty().set(false);
        defaultStyleButton.disableProperty().set(false);
        functionButton.disableProperty().set(false);
        incrementButton.disableProperty().set(false);
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
        counter = 0;
    }

    private String convertFromPos(Pos pos) {
        return switch (pos) {
            case CENTER_LEFT -> "Left";
            case CENTER_RIGHT -> "Right";
            default -> "Center";
        };
    }

    public void onSort() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/sort/createSortDialog.fxml"));
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
    }


    @FXML
    public void onFilter() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/filter/createFilterDialog.fxml"));
        Parent root = loader.load();
        FilterDialogController controller = loader.getController();
        controller.setAppController(appController);
        controller.setBoundaries(appController.getNumOfRows(), appController.getNumOfCols());
        controller.fillData();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setTitle("Filter");
        controller.setDialogStage(dialogStage);
        controller.setAppController(appController);
        dialogStage.showAndWait();
    }

    @FXML
    public void addRangeAction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/range/createRangeDialog.fxml"));
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
    }

    private void handleRangeSelected(String selectedItem) {
        appController.PaintCells(appController.GetRange(selectedItem).getRangeCellsCoordinate(), "pink");
    }

    public void addRangesToComboBox(List<String> ranges) {
        clearRangeComboBox();
        rangesComboBox.getItems().add("Range");
        ranges.forEach(this::addRangeToComboBox);

        rangesComboBox.setOnAction(event -> {
            String selectedRange = rangesComboBox.getSelectionModel().getSelectedItem();
            if (!Objects.equals(selectedRange, rangesComboBox.getItems().getFirst())) {
                appController.removePaint();
                handleRangeSelected(selectedRange);

                minus.setOnMouseClicked(mouseEvent -> {
                    appController.removePaint();
                    try {
                        appController.removeRange(selectedRange);
                        rangesComboBox.getItems().remove(selectedRange);
                    } catch (Exception e) {
                        AppController.showError(e.getMessage());
                    }
                });
            } else {
                appController.removePaint();
            }
        });
    }

    private void addRangeToComboBox(String rangeName) {
        rangesComboBox.getItems().add(rangeName);
    }

    private void clearRangeComboBox() {
        rangesComboBox.getItems().clear();
    }

    public void addVersion() {
        SheetVersionComboBox.getItems().add(String.valueOf(SheetVersionComboBox.getItems().size()));
    }

    public void clearVersion() {
        SheetVersionComboBox.getItems().clear();
        SheetVersionComboBox.getItems().add("Version");
    }

    private void handleVersionSelected(String selectedItem) {
        SheetDto sheetByVersionVersion = appController.getSheetByVersion(Integer.parseInt(selectedItem));
        appController.createNewSheetInDifferentWindows(sheetByVersionVersion);
    }

    @FXML
    public void onDefaultStyle() {
        appController.setDefaultStyle(CoordinateFactory.getCoordinate(cellId.get()));
    }

    @FXML
    public void onFunction() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/component/top/dialog/function/createFunctionDialog.fxml"));
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


    public void clickOnIncrementButton() {
        if(!fromTextField.getText().isEmpty() && !toTextField.getText().isEmpty() && !stepTextField.getText().isEmpty()) {
            int from = Integer.parseInt(fromTextField.getText());
            int to = Integer.parseInt(toTextField.getText());
            int step = Integer.parseInt(stepTextField.getText());

            if(from > to || to - from < step){
                AppController.showError("Invalid increment");
            }
            else{
                counter = (from + step * counter) > to ?  0 : counter;
                String toCalc = String.valueOf(from + step * counter );
                appController.applyDynamicCalculate(CoordinateFactory.getCoordinate(cellId.getValue()), toCalc);
                counter++;
            }
        }
    }
}


