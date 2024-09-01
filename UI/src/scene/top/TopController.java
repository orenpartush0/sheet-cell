package scene.top;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import scene.app.AppController;

import java.io.File;

public class TopController {

    private AppController appController;

    String previousPath;

    @FXML
    private TextField path;

    @FXML
    private TextField originalValue;

    @FXML
    private TextField lastUpdate;

    @FXML
    private TextField cellId;

    @FXML
    private Label cellIdLabel;

    @FXML
    private Label originalValueLabel;

    @FXML
    private TextField updateValueField;

    @FXML
    private Label lastUpdatedVersionLabel;

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

    @FXML
    private void handleUpdateValue() {
        // Function body here
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

    public void handleLoadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            previousPath = path.getText();
            path.setText(filePath);
            appController.importFileFromXml(filePath);
        }
    }

    public void setPreviousPath(){
        path.setText(previousPath);
    }

}



