package dashboard;

import Connector.Connector;
import dashboard.dialog.sheet.SheetDialogController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;


public class DashBoardController {

    @FXML
    public TableView requestTable;

    @FXML
    public Label userNameLabel;

    @FXML
    public TextField selectedSheet;

    @FXML
    private Button viewSheetButton;

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button ackDenyPermissionButton;

    @FXML
    private TableColumn<SheetData, String> ownerColumn;

    @FXML
    private TableColumn<SheetData, String> sheetNameColumn;

    @FXML
    private TableColumn<SheetData, String> sizeColumn;

    @FXML
    private TableColumn<SheetData, String> permissionCol;

    @FXML
    private TableView<PermissionRequest> sheetsTable;

    @FXML
    private TableColumn<PermissionRequest, String> userColumn;

    @FXML
    private TableColumn<PermissionRequest, String> permissionColumn;

    @FXML
    private TableColumn<PermissionRequest, String> statusColumn;

    private Stage stage;

    private ObservableList<SheetData> sheetData;
    private ObservableList<PermissionRequest> permissionRequests;
    private StringProperty userNameProp = new SimpleStringProperty();

    @FXML
    public void initialize() {
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        permissionCol.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        permissionColumn.setCellValueFactory(new PropertyValueFactory<>("permission"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        userNameLabel.textProperty().bind(userNameProp);

        sheetData = FXCollections.observableArrayList(
                new SheetData("Alice", "SheetData 1", "12x12", "Read"),
                new SheetData("Bob", "SheetData 2", "10x10", "Write")
        );
        requestTable.setItems(sheetData);

        permissionRequests = FXCollections.observableArrayList(
                new PermissionRequest("Alice", "Read", "Approved"),
                new PermissionRequest("Bob", "Write", "Pending")
        );
        sheetsTable.setItems(permissionRequests);

        viewSheetButton.setOnAction(e -> viewSheet());
        requestPermissionButton.setOnAction(e -> requestPermission());
        ackDenyPermissionButton.setOnAction(e -> ackDenyPermission());
    }

    public void setStage(Stage _stage) {
        stage = _stage;
    }

    public void setName(String name){
        userNameProp.set(name);
    }

    private void viewSheet() {

    }

    private void requestPermission() {

    }

    private void ackDenyPermission() {

    }

    public static class SheetData {
        private final String owner;
        private final String sheetName;
        private final String size;
        private final String permissionType;

        public SheetData(String owner, String sheetName, String size, String permissionType) {
            this.owner = owner;
            this.sheetName = sheetName;
            this.size = size;
            this.permissionType = permissionType;
        }

        public String getOwner() {
            return owner;
        }

        public String getSheetName() {
            return sheetName;
        }

        public String getSize() {
            return size;
        }

        public String getPermissionType() {
            return permissionType;
        }
    }

    public static class PermissionRequest {
        private final String user;
        private final String permission;
        private final String status;

        public PermissionRequest(String user, String permission, String status) {
            this.user = user;
            this.permission = permission;
            this.status = status;
        }

        public String getUser() {
            return user;
        }

        public String getPermission() {
            return permission;
        }

        public String getStatus() {
            return status;
        }
    }

    public void setUser(String userName){
        userNameProp.set(userName);
    }

    @FXML
    private void handleLoadFile() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML or BIN Files", "*.xml", "*.bin"));
        File initialDirectory = new File(System.getProperty("user.home"));
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();
            Connector.SetSheet(filePath);
        }
    }

    @FXML
    public void handleCreateNewSheet() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboard/dialog/sheet/createSheetDialog.fxml"));
        Parent root = loader.load();
        SheetDialogController controller = loader.getController();
        Stage dialogStage = new Stage();
        dialogStage.setTitle("SheetData Details");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setScene(new Scene(root));
        dialogStage.setHeight(240);
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();
    }
}
