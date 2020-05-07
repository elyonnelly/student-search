package controllers;

import api.StudentSearchApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OverviewController extends Controller implements Initializable {

    private String fileListTitle;

    @FXML
    private Label userName;

    @FXML
    private VBox participants;

    @FXML
    private VBox prizers;

    @FXML
    private VBox winners;

    @FXML
    private Label listTitle;

    @FXML
    private TextField targetCourse;

    @FXML
    private Label numberOfSubscriber;

    @FXML
    private HBox commonPublic;

    public OverviewController(Stage stage, StudentSearchApp app, String fileListTitle) {
        super(stage, app);
        this.fileListTitle = fileListTitle;
    }

    @FXML
    void onActionBack() {
        viewScene("menuScene.fxml", new MenuController(stage, app));
    }

    @FXML
    void onActionUpdateButton() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
        String path = "src/main/resources/data/";
        loadListOfUser(path, fileListTitle);
    }

    private void loadListOfUser(String path, String listName) {
        try (BufferedReader winnersReader = new BufferedReader(new FileReader(path + "Winners"+listName+".txt"));
             BufferedReader prizerReader = new BufferedReader(new FileReader(path + "Prizewinners"+listName+".txt"));
             BufferedReader participantReader = new BufferedReader(new FileReader(path + "Participant"+listName+".txt"))) {
            uploadData(winnersReader, winners);
            uploadData(prizerReader, prizers);
            uploadData(participantReader, participants);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadData(BufferedReader reader, VBox container) throws IOException {
        ListView<String> listView = new ListView<>();
        String id = reader.readLine();
        while(id != null) {
            listView.getItems().add(id);
            id = reader.readLine();
        }
        container.getChildren().add(listView);
    }

    private void setName(String name) {
        userName.setText(name);
    }
}
