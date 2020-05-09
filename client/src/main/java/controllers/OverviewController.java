package controllers;

import api.StudentSearchApp;
import controllers.task.commands.CountGroupMemberTaskCommand;
import controllers.task.commands.ResponseTask;
import controllers.task.commands.SearchCommonPublicTaskCommand;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class OverviewController extends Controller implements Initializable {

    private String fileListTitle;
    private List<Integer> userdId;
    private Task activeTask;

    //region FXML fields
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
    private VBox commonPublic;

    @FXML
    private Label searchPublicStatus;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextField numberOfResult;

    //endregion

    public OverviewController(Stage stage, StudentSearchApp app, String fileListTitle) {
        super(stage, app);
        this.fileListTitle = fileListTitle;
        userdId = new ArrayList<>();
    }

    @FXML
    void onActionBack() {
        showScene("menuScene.fxml", new MenuController(stage, app));
    }

    @FXML
    void onActionUpdateButton() {
        if (targetCourse.getText().equals("")) {
            showMessage("Не задана группа курсов! Укажите id");
            return;
        }
        activeTask = new ResponseTask(app, new CountGroupMemberTaskCommand(app, userdId, targetCourse.getText()));
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(activeTask.progressProperty());
        activeTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                showMessage("Done!");
                numberOfSubscriber.setText(String.valueOf(activeTask.getValue()));
                progressBar.setVisible(false);
            }
        });
        new Thread(activeTask).start();
    }

    @FXML
    void onActionSearchCommonPublic() {
        activeTask = new ResponseTask(app, new SearchCommonPublicTaskCommand(app, userdId));
        progressBar.setVisible(true);
        progressBar.progressProperty().bind(activeTask.progressProperty());
        activeTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                showMessage("Done!");
                var result = (ArrayList<Map.Entry<Integer, Integer>>)activeTask.getValue();
                fillCommonPublic(result);
                progressBar.setVisible(false);
            }
        });
        new Thread(activeTask).start();
    }

    private void fillCommonPublic(ArrayList<Map.Entry<Integer, Integer>> data) {
        int number;
        if (numberOfResult.getText().equals("")) {
            number = data.size();
        }
        else {
            number = Integer.parseInt(numberOfResult.getText());
        }
        var id = new ListView();
        id.getItems().add("id паблика   количество");
        for (var item : data) {
            id.getItems().add(item.getKey() + " " + item.getValue());
        }
        commonPublic.getChildren().add(id);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setName(app.getUserName());
        listTitle.setText(fileListTitle);
        String path = "src/main/resources/data/";
        loadListOfUser(path, fileListTitle);
    }

    private void renameList(String oldTitle, String newTitle) {
        String path = "src/main/resources/data/";
        //найти среди listTitles oldTitle
        renameListTitle(path, oldTitle, newTitle);
        //найти среди файлов файлы с названием oldTitle. сменить название.
        var oldFileNames = StudentSearchApp.buildNames(path, oldTitle);
        var newFileNames = StudentSearchApp.buildNames(path, newTitle);
        for (int i = 0; i < oldFileNames.size(); i++) {
            File file = new File(oldFileNames.get(0));
            if (!file.renameTo(new File(newFileNames.get(0)))) {
                showMessage("Не удается переименовать файл");
            }
        }
    }

    private void renameListTitle(String path, String oldTitle, String newTitle) {
        StringBuilder titles = new StringBuilder();
        try (BufferedReader titlesReader = new BufferedReader(new FileReader(path + "listTitles.txt"))) {
            String title = titlesReader.readLine();
            while(title != null) {
                if (title.equals(oldTitle)) {
                    titles.append(newTitle).append("\n");
                }
                else {
                    titles.append(title);
                }
                title = titlesReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (FileWriter writer = new FileWriter(path + "listTitles.txt")) {
            writer.write(titles.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadListOfUser(String path, String listName) {
        var fileNames = StudentSearchApp.buildNames(path, listName);
        List<VBox> containers = Arrays.asList(winners, prizers, participants);
        for (int i = 0; i < fileNames.size(); i++) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileNames.get(i)))) {
                uploadData(reader, containers.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadData(BufferedReader reader, VBox container) throws IOException {
        ListView<String> listView = new ListView<>();
        String id = reader.readLine();
        while(id != null) {
            listView.getItems().add(id);
            userdId.add(Integer.valueOf(id));
            id = reader.readLine();
        }
        container.getChildren().add(listView);
    }

    private void setName(String name) {
        userName.setText(name);
    }
}
