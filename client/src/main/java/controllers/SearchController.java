package controllers;

import api.StudentSearchApp;
import api.parse.Query;
import controllers.task.commands.FindUsersTaskCommand;
import controllers.task.commands.ResponseTask;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import views.SearchView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchController extends SearchView implements Initializable {

    private File chooseFile;
    private String fileType;
    private List<List<Integer>> resultOfSearch;
    private Task activeTask;
    private List<Query> resultOfHandling;

    SearchController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionBack() {
        showScene("menuScene.fxml", new MenuController(stage, app));
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
        selectFields.getSelectionModel().clearSelection();
    }

    @FXML
    void onActionRemoveField() {
        var selectionItems = fieldList.getSelectionModel().getSelectedItems();
        fieldList.getItems().removeAll(selectionItems);
        fieldList.getSelectionModel().clearSelection();
    }

    @FXML
    void onActionStopSearch() {
        activeTask.cancel();
    }

    @FXML
    void onActionShowSearchResult() {
        blockButtons(false);
        resultOfSearch = (List<List<Integer>>) activeTask.getValue();
        Stage stage = new Stage();
        showStage(stage, "Результаты поиска",
                    "../resultSearchScene.fxml",
                                new ResultController(stage, app, resultOfSearch, listTitle.getText()));
    }

    @FXML
    void onActionHandleFile() {
        if (fileDataExists()) {
            if (fileType.equals("csv")) {
                handleCsvFile();
            }
            if (fileType.equals("pdf")) {
                parsePdfFile();
            }
        }
    }

    void loadAnotherFile() {
        showScene("searchScene.fxml", this);
        closeChildrenStage();
    }

    void startSearch() {
        startSearch(resultOfHandling);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
        initializeSelectFields();
        fieldList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        fieldList.setItems(FXCollections.observableArrayList());
        fieldList.setEditable(true);
    }

    void handlePdfFile(List<String> lines) {
        try {
            resultOfHandling = app.handlePdfData(lines, fieldList.getItems());
            showHandlingResult(resultOfHandling);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    private void handleCsvFile() {
        try {
            resultOfHandling = app.handleCsvData(chooseFile, fieldList.getItems());
            showHandlingResult(resultOfHandling);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    private void parsePdfFile() {
        try {
            var fileStrings = app.parsePdfByLine(chooseFile);
            Stage stage = new Stage();
            showStage(stage, "Содержимое файла", "../chooseRanges.fxml", new ChooseRangeController(this, stage, app, fileStrings));
        } catch (IOException e) {
            e.printStackTrace();
            showMessage(e.getMessage());
        }
    }

    private void startSearch(List<Query> queries) {
        blockButtons(true);

        activeTask = new ResponseTask(app, new FindUsersTaskCommand(app, queries, listTitle.getText(), append.isSelected()));
        progressBar.progressProperty().bind(activeTask.progressProperty());
        progressLabel.textProperty().bind(activeTask.messageProperty());
        activeTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                showMessage("Обработка завершена!");
                showResult.setDisable(false);
                blockButtons(false);
            }
        });

        new Thread(activeTask).start();
    }

    private boolean fileDataExists() {
        if (chooseFile == null) {
            showMessage("Файл не загружен!");
            return false;
        }
        if (fieldList.getItems().size() == 0) {
            showMessage("Поля не выбраны!");
            return false;
        }
        if (listTitle.getText().equals("")) {
            listTitle.setText(chooseFile.getName());
        }
        return true;
    }

}
