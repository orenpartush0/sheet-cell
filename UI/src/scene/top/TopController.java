package scene.top;

import dto.CellDto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scene.app.AppController;
import scene.top.dialog.SheetDialogController;
import shticell.sheet.coordinate.Coordinate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

public class TopController {

    private AppController appController;

    String previousPath;

    @FXML
    public MenuItem saveButton;

    @FXML
    public ComboBox<String> comboBox;

    @FXML
    private TextField path;

    @FXML
    private TextField originalValue;

    @FXML
    private TextField lastUpdate;

    @FXML
    private TextField cellId;

    @FXML
    public ComboBox Ranges;

    @FXML
    private TextField updateValueField;

    @FXML
    private ScrollPane versionScrollPane;

    @FXML
    public ComboBox SheetVersion;

    @FXML
    private VBox versionList;

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    @FXML
    private void comboBoxVersionList() {
    }

    public void loadVersions(String[] versions) {
        // Function body here
    }


    private void handleVersionSelect(String version) {
        // Function body here
    }

    public void loadData(String cellId, String originalValue, String lastUpdatedVersion) {
        // Function body here
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
            previousPath = path.getText();
            path.setText(filePath);
            appController.importFile(filePath);
        }
    }

    @FXML
    void HandleSaveFile() throws IOException {
        if(path.getText().endsWith(".xml") || path.getText().isEmpty()) {
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
        }
        else
        {
            File file = new File(path.getText());
            try (FileWriter writer = new FileWriter(file)) {}
            catch (IOException ignored) {}
            appController.saveSheet(path.getText());
        }
    }

    @FXML
    public void handleCreateFile() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dialog/createSheetDialog.fxml"));
        Parent root = loader.load();

        SheetDialogController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Sheet Details");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setScene(new Scene(root));
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
        if (controller.isOkClicked()) {
            String sheetName = controller.getSheetName();
            int numColumns = controller.getNumColumns();
            int numRows = controller.getNumRows();
            appController.createNewSheet(sheetName,numColumns,numRows);
        }
    }

    public void setPreviousPath(){
        path.setText(previousPath);
    }

    public void setSaveButtonAble(){
        saveButton.setDisable(false);
    }

    public void setOnMouseCoordinate(CellDto cell){
        cellId.setText(cell.coordinate().toString());
        originalValue.setText(cell.originalValue());
        lastUpdate.setText(String.valueOf(cell.LatestSheetVersionUpdated()));
    }

}



