package controllers;

import api.StudentSearchApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class InfoController extends Controller implements Initializable {

    @FXML
    Label userName;

    InfoController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionBack() {
        showScene("menuScene.fxml", new MenuController(stage, app));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userName.setText(app.getUserName());
    }
}
