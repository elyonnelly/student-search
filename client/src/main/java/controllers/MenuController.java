package controllers;

import api.StudentSearchApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuController extends Controller implements Initializable {

    @FXML
    protected Label userName;


    public MenuController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionGoToResults() {
        viewScene("overviewScene.fxml", new OverviewController(stage, app, ""));
    }

    @FXML
    void onActionGoToSearch() {
        userName.setText("hello");
        viewScene("searchScene.fxml", new SearchController(stage, app));
    }

    @FXML
    void onActionGoToAboutProgram() {
        viewScene("aboutProgramScene.fxml", new InfoController(stage, app));
    }

    @FXML
    void onActionContacts() {
        viewScene("contacts.fxml", new InfoController(stage, app));
    }

    @FXML
    void onActionBack() {
        viewScene("authScene.fxml", new AuthController(stage, app));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
    }

    private void setName(String name) {
        userName.setText(name);
    }
}
