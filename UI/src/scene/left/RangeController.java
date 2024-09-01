package scene.left;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import scene.app.AppController;


public class RangeController {

    private AppController appController;

    @FXML
    private Accordion accordion;

    public void setAppController(AppController _appController) {
        this.appController = _appController;
    }


}
