package scene.sheet;

import dto.SheetDto;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import scene.app.AppController;

public class SheetController {

    private AppController appController;

    @FXML
    private ScrollPane borderPane;

    @FXML
    private GridPane gridPaneTop;

    @FXML
    private GridPane gridPaneLeft;

    @FXML
    private GridPane gridPaneSheet;

    @FXML
    public ScrollPane upDownScroller;

    @FXML
    public ScrollPane rightLeftScroller;


    public void initialize() {
        upDownScroller.vvalueProperty().addListener((obs, oldVal, newVal) -> gridPaneLeft.setLayoutY(-newVal.doubleValue() * (gridPaneSheet.getHeight() - upDownScroller.getViewportBounds().getHeight())));
        rightLeftScroller.hvalueProperty().addListener((obs, oldVal, newVal) -> {
            gridPaneTop.setLayoutX(Math.max(0, -newVal.doubleValue() * (gridPaneSheet.getWidth() - rightLeftScroller.getViewportBounds().getWidth())));
        });
    }

    public void setAppController(AppController appController) {
        this.appController = appController;
    }

    private void clearSheet() {
        gridPaneSheet.getChildren().clear();
        gridPaneTop.getChildren().clear();
        gridPaneLeft.getChildren().clear();
    }

    public void fillSheet(SheetDto sheet) {
        clearSheet();

        // Add row numbers to the left GridPane
        for (int row = 1; row <= sheet.numberOfRows(); row++) {
            Label rowLabel = new Label(String.valueOf(row));
            rowLabel.setPrefWidth(50);  // Adjust width as necessary
            rowLabel.setPrefHeight(30);  // Adjust height as necessary
            rowLabel.setAlignment(Pos.CENTER);  // Center the text within the label
            gridPaneLeft.add(rowLabel, 0, row);

            // Center the label in the GridPane cell
            GridPane.setHalignment(rowLabel, HPos.CENTER);
            GridPane.setValignment(rowLabel, VPos.CENTER);
        }

        // Add column letters to the top GridPane
        for (int col = 0; col <= sheet.numberOfColumns(); col++) {
            Label colLabel = new Label(getColumnLabel(col));
            colLabel.setPrefWidth(100);  // Adjust width to match cell width
            colLabel.setPrefHeight(30);  // Adjust height as necessary
            colLabel.setAlignment(Pos.CENTER);  // Center the text within the label
            gridPaneTop.add(colLabel, col, 0);

            // Center the label in the GridPane cell
            GridPane.setHalignment(colLabel, HPos.CENTER);
            GridPane.setValignment(colLabel, VPos.CENTER);
        }
    }


    private String getColumnLabel(int colIndex) {
        StringBuilder columnLabel = new StringBuilder();
        while (colIndex > 0) {
            colIndex--;
            columnLabel.insert(0, (char) ('A' + (colIndex % 26)));
            colIndex /= 26;
        }
        return columnLabel.toString();
    }
}
