import api.StudentSearchApp;
import api.query.Query;
import api.search.MessageType;
import api.search.SearchSubscriber;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController extends Controller implements Initializable {

    private File chooseFile;
    private String fileType;
    private List<List<Integer>> resultOfSearch;
    private Task activeTask;

    SearchController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionBack() {
        viewScene("menuScene.fxml", new MenuController(stage, app));
    }

    @FXML
    /*
     * Обработчик для кнопки загрузки файла
     */
    void loadFile() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            var name = file.getName();
            if (name.matches(".*\\.pdf")) {
                fileType = "pdf";
            }
            else if (name.matches(".*\\.csv") || name.matches(".*\\.CSV")) {
                fileType = "csv";
            }
            else {
                showMessage("Можно загрузить только файл формата PDF или CSV.");
                return;
            }
            chooseFile = file;
            setLoadFileName(file.getName());
        }
    }

    @FXML
    void chooseField() {
        addField(selectFields.getValue());
    }

    @FXML
    void onActionRemoveField() {
        var selectionItems = fieldList.getSelectionModel().getSelectedItems();
        fieldList.getItems().removeAll(selectionItems);
    }

    private void startSearch(List<Query> queries) {
        progressBar.setDisable(false);
        stopSearch.setDisable(false);
        //TODO: заблокировать временно загрузку нового файла и нажатия на кнопки.
        activeTask = new SearchTask(app, queries, listTitle.getText());
        progressBar.progressProperty().bind(activeTask.progressProperty());
        progressLabel.textProperty().bind(activeTask.messageProperty());
        new Thread(activeTask).start();
    }

    @FXML
    void onActionStopSearch() {
        activeTask.cancel();
    }

    @FXML
    void onActionShowResult() {
        System.out.println(activeTask.getProgress());
        System.out.println(activeTask.totalWorkProperty().getValue());
        if (activeTask.getProgress() != activeTask.totalWorkProperty().getValue()) {
            return;
        }
        System.out.println("okay go!");
    }

    @FXML
    void handleFile() {
        startSearch(null);
        if (chooseFile == null) {
            showMessage("Файл не загружен!");
            return;
        }
        if (fieldList.getItems().size() == 0) {
            showMessage("Поля не выбраны!");
            return;
        }
        if (listTitle == null || listTitle.getText().equals("")) {
            showMessage("Не указано название списка!");
            return;
        }

        if (fileType.equals("csv")) {
            List<Query> queries = null;
            /*try {
                //queries = app.handleCsvData(chooseFile, fieldList.getItems());
            } catch (IOException e) {
                showMessageError(e.getMessage());
                return;
            }*/
            startSearch(queries);

            showMessage("Файлы созданы!");
        }
        if (fileType.equals("pdf")) {
            showMessage("Заглуше4ка");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
        initializeSelectFields();
        fieldList.setItems(FXCollections.observableArrayList());
    }

}
