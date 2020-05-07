import api.StudentSearchApp;
import api.parse.Query;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
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
    void onActionLoadFile() {
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
    void onActionChooseField() {
        addField(selectFields.getValue());
    }

    @FXML
    void onActionRemoveField() {
        var selectionItems = fieldList.getSelectionModel().getSelectedItems();
        fieldList.getItems().removeAll(selectionItems);
    }

    @FXML
    void onActionStopSearch() {
        activeTask.cancel();
    }

    @FXML
    void onActionShowResult() {
        //TODO сбиндить свойство Disable у кнопки showResult и значение (activeTask.getProgress() != 1)
        if (activeTask.getProgress() != 1) {
            return;
        }
        blockButtons(false);
        resultOfSearch = (List<List<Integer>>) activeTask.getValue();
        openResultSearchStage();
    }

    @FXML
    void onActionHandleFile() {
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

    private void openResultSearchStage() {
        Stage resultSearchStage = new Stage();
        resultSearchStage.setTitle("Результаты поиска");
        var loader = new FXMLLoader(getClass().getResource("resultSearchScene.fxml"));
        loader.setController(new ResultController(resultSearchStage, app, resultOfSearch, listTitle.getText()));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultSearchStage.setScene(scene);
        resultSearchStage.show();
    }


    private void blockButtons(boolean state) {
        progressBar.setDisable(!state);
        stopSearch.setDisable(!state);
        selectFields.setDisable(state);
        removeField.setDisable(state);
        listTitle.setDisable(state);
    }

    private void startSearch(List<Query> queries) {
        blockButtons(true);

        activeTask = new SearchTask(app, queries, listTitle.getText());
        progressBar.progressProperty().bind(activeTask.progressProperty());
        progressLabel.textProperty().bind(activeTask.messageProperty());

        new Thread(activeTask).start();
    }

}
