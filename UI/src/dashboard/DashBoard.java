package dashboard;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class DashBoard {

    @FXML
    private Button viewSheetButton;

    @FXML
    private Button requestPermissionButton;

    @FXML
    private Button ackDenyPermissionButton;

    @FXML
    private TableView<Sheet> permissionTable;

    @FXML
    private TableColumn<Sheet, String> ownerColumn;

    @FXML
    private TableColumn<Sheet, String> sheetNameColumn;

    @FXML
    private TableColumn<Sheet, String> sizeColumn;

    @FXML
    private TableColumn<Sheet, String> permissionCol;

    @FXML
    private TableView<Permission> sheetsTable;

    @FXML
    private TableColumn<Permission, String> userColumn;

    @FXML
    private TableColumn<Permission, String> permissionColumn;

    @FXML
    private TableColumn<Permission, String> statusColumn;

    @FXML
    private Label user;

    private ObservableList<Sheet> sheets;
    private ObservableList<Permission> permissions;
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

        user.textProperty().bind(userNameProp);

        sheets = FXCollections.observableArrayList(
                new Sheet("Alice", "Sheet 1", "12x12", "Read"),
                new Sheet("Bob", "Sheet 2", "10x10", "Write")
        );
        permissionTable.setItems(sheets);

        permissions = FXCollections.observableArrayList(
                new Permission("Alice", "Read", "Approved"),
                new Permission("Bob", "Write", "Pending")
        );
        sheetsTable.setItems(permissions);

        viewSheetButton.setOnAction(e -> viewSheet());
        requestPermissionButton.setOnAction(e -> requestPermission());
        ackDenyPermissionButton.setOnAction(e -> ackDenyPermission());
    }

    private void viewSheet() {

    }

    private void requestPermission() {

    }

    private void ackDenyPermission() {

    }

    public static class Sheet {
        private final String owner;
        private final String sheetName;
        private final String size;
        private final String permissionType;

        public Sheet(String owner, String sheetName, String size, String permissionType) {
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

    public static class Permission {
        private final String user;
        private final String permission;
        private final String status;

        public Permission(String user, String permission, String status) {
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
}
