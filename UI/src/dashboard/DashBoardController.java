package dashboard;

import Connector.Connector;
import constant.Constants;
import dashboard.dialog.sheet.SheetDialogController;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import shticell.manager.enums.PermissionStatus;
import shticell.manager.enums.PermissionType;
import util.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;


public class DashBoardController {

    @FXML
    public TableView<PermissionRequest> requestTable;

    @FXML
    public Label userNameLabel;

    @FXML
    public TextField selectedSheet;

    @FXML
    public ComboBox permissionComboBox;

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
    private TableView<SheetData> sheetsTable;

    @FXML
    private TableColumn<PermissionRequest, String> userColumn;

    @FXML
    private TableColumn<PermissionRequest, String> permissionColumn;

    @FXML
    private TableColumn<PermissionRequest, String> statusColumn;

    private Stage stage;

    private ObservableList<SheetData> sheetData;
    private ObservableList<PermissionRequest> permissionRequests;
    private Timer timer = new Timer();

    //Properties
    private final SimpleStringProperty selectedSheetTextProp = new SimpleStringProperty("");
    private final SimpleStringProperty userNameProp = new SimpleStringProperty();
    private final SimpleBooleanProperty viewSheetEnableProp = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty requestPermissionEnableProp = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty comboBoxEnableProp = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty ackDenyEnableProp = new SimpleBooleanProperty(false);

    @FXML
    public void initialize() {
        permissionComboBox.disableProperty().bind(comboBoxEnableProp.not());
        requestPermissionButton.disableProperty().bind(requestPermissionEnableProp.not());
        ackDenyPermissionButton.disableProperty().bind(ackDenyEnableProp.not());
        viewSheetButton.disableProperty().bind(viewSheetEnableProp.not());
        selectedSheet.textProperty().bind(selectedSheetTextProp);
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));
        sheetNameColumn.setCellValueFactory(new PropertyValueFactory<>("sheetName"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        permissionCol.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));
        permissionColumn.setCellValueFactory(new PropertyValueFactory<>("permissionType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("permissionStatus"));
        userNameLabel.textProperty().bind(userNameProp);
        viewSheetButton.setOnAction(e -> viewSheet());
        requestPermissionButton.setOnAction(e -> requestPermission());
        ackDenyPermissionButton.setOnAction(e -> ackDenyPermission());
        sheetsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            SheetData sheetData = sheetsTable.getSelectionModel().getSelectedItem();
            if (sheetData != null) {
                clickOnSheetHandle(sheetData.sheetName);
                viewSheetEnableProp.set(sheetData.permissionType.getPermissionLevel() >= PermissionType.READER.getPermissionLevel());
                comboBoxEnableProp.set(sheetData.permissionType != PermissionType.OWNER);
                selectedSheetTextProp.set(sheetData.sheetName);

                if(comboBoxEnableProp.get()) {
                    int oldSelectionIndex = permissionComboBox.getSelectionModel().getSelectedIndex();
                    permissionComboBox.getItems().clear();
                    Arrays.stream(PermissionType.values()).
                            filter(permissionType -> permissionType != sheetData.permissionType).
                            filter(permissionType -> permissionType != PermissionType.OWNER).
                            forEach(permissionType ->permissionComboBox.getItems().add(permissionType) );

                    if(oldSelectionIndex >= 0 ){
                        permissionComboBox.getSelectionModel().select(oldSelectionIndex);
                    }
                }else{
                    requestPermissionEnableProp.set(false);
                }
            }
        });
        setSheetListRefresher();
        permissionComboBox.setOnAction(e-> requestPermissionEnableProp.set(true));

        requestTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            SheetData sheetData = sheetsTable.getSelectionModel().getSelectedItem();
            ackDenyEnableProp.set(sheetData != null && sheetData.permissionType == PermissionType.OWNER);
        });
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
        SheetData sheetData = sheetsTable.getSelectionModel().getSelectedItem();
        if(permissionComboBox.getSelectionModel().getSelectedItem() != null){
            PermissionType permissionType = PermissionType.valueOf(permissionComboBox.getSelectionModel().getSelectedItem().toString());
            if (sheetData != null) {
                System.out.println(sheetData.sheetName);
                HttpClientUtil.runAsync( Constants.ADD_REQUEST + "?" + Constants.SHEET + "=" + sheetData.sheetName,
                        Constants.PUT, permissionType, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response){
                            }
                        });
            }
        }

    }

    private void ackDenyPermission() {

    }

    private void setSheetListRefresher(){
        ListRefresher sheetlistRefresher = new ListRefresher<>(
                Constants.SHEET_DASHBOARD,
                this::updateSheetDataTable,
                SheetData[].class
        );
        new Timer().schedule(sheetlistRefresher,0,1000);
    }

    private void updateSheetDataTable(List<SheetData> sheetDataLst){
        Platform.runLater(() -> {
            int oldSelectedIndex = sheetsTable.getSelectionModel().getSelectedIndex();
            ObservableList<SheetData> items = sheetsTable.getItems();
            items.clear();
            items.addAll(sheetDataLst);
            if(oldSelectedIndex >= 0){
                sheetsTable.getSelectionModel().select(oldSelectedIndex);
            }
        });
    }

    public static class SheetData {
        private final String owner;
        private final String sheetName;
        private final String size;
        private final PermissionType permissionType;

        public SheetData(String owner, String sheetName, String size, PermissionType permissionType) {
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
            return permissionType.toString();
        }
    }

    public static class PermissionRequest {
        private final String user;
        private final PermissionType permissionType;
        private final PermissionStatus permissionStatus;

        public PermissionRequest(String user, PermissionType _permissionType, PermissionStatus _permissionStatus ) {
            this.user = user;
            this.permissionType = _permissionType;
            this.permissionStatus = _permissionStatus;
        }

        public String getUser() {
            return user;
        }

        public String getPermissionType() {
            return permissionType.toString();
        }

        public String getPermissionStatus() {
            return permissionStatus.toString();
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

    private void clickOnSheetHandle(String sheetName){
        timer.cancel();
        timer = new Timer();
        ListRefresher sheetlistRefresher = new ListRefresher<>(
                Constants.REQUEST_DASHBOARD + "?" + Constants.SHEET + "=" + sheetName,
                this::updateRequestDataTable,
                PermissionRequest[].class
        );
        timer.schedule(sheetlistRefresher,0,2000);
    }

    private void updateRequestDataTable(List<PermissionRequest> permissionRequestsLst){
        Platform.runLater(() -> {
            int oldSelectedIndex = requestTable.getSelectionModel().getSelectedIndex();
            ObservableList<PermissionRequest> items = requestTable.getItems();
            items.clear();
            items.addAll(permissionRequestsLst);
            if(oldSelectedIndex >= 0){
                requestTable.getSelectionModel().select(oldSelectedIndex);
            }
        });
    }
}
