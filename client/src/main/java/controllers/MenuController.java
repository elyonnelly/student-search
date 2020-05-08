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
        showScene("listsScene.fxml", new ListsController(stage, app));
    }

    @FXML
    void onActionGoToSearch() {
        userName.setText("hello");
        showScene("searchScene.fxml", new SearchController(stage, app));
    }

    @FXML
    void onActionGoToAboutProgram() {
        showScene("aboutProgramScene.fxml", new InfoController(stage, app));
    }

    @FXML
    void onActionContacts() {
        showScene("contacts.fxml", new InfoController(stage, app));
    }

    @FXML
    void onActionBack() {
        showScene("authScene.fxml", new AuthController(stage, app));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
    }

    private void setName(String name) {
        userName.setText(name);
    }
}
