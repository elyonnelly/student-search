import api.StudentSearchApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OverviewController extends Controller implements Initializable {
    OverviewController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionBack() {
        viewScene("menuScene.fxml", new MenuController(stage, app));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
    }
}
