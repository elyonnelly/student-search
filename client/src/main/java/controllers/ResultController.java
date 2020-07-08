package controllers;

import api.StudentSearchApp;
import api.parse.StatusParticipant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Контроллер сцены с отображением результатов поиска
 */
public class ResultController extends Controller implements Initializable {

    private List<List<Integer>> results;
    private String listTitle;


    @FXML
    private HBox wrapperTableResult;

    @FXML
    private Label numberOfResult;

    public ResultController(Stage stage, StudentSearchApp app, List<List<Integer>> results, String listTitle) {
        super(stage, app);
        this.results = results;
        this.listTitle = listTitle;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeScene(results);
    }

    //@FXML
    void onActionDownloadFiles() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        try {
            app.saveFile(results, listTitle, selectedDirectory.getPath() + "\\", false, true);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Не удается сохранить файлы!");
        }
        showMessage("Файлы сохранены!");
    }


    private void initializeScene(List<List<Integer>> resultOfSearch) {
        int number = 0;
        for (int i = 0; i < 3; i++) {
            if (resultOfSearch.get(i).size() != 0) {
                number += resultOfSearch.get(i).size();
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);

                Label handler = new Label(StatusParticipant.values()[i].getStringValue());
                vBox.getChildren().add(handler);

                ListView<Integer> listView = new ListView<>();
                listView.setItems(FXCollections.observableList(resultOfSearch.get(i)));
                vBox.getChildren().add(listView);

                wrapperTableResult.getChildren().add(vBox);
            }
        }
        numberOfResult.setText(String.valueOf(number));
    }
}

