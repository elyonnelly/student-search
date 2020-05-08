package controllers;

import api.StudentSearchApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListsController extends Controller implements Initializable {

    @FXML
    VBox listsContainer;

    public ListsController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //открываем файл
        try (BufferedReader titles = new BufferedReader(new FileReader("src/main/resources/data/listTitles.txt"))) {
            String title = titles.readLine();
            while(title != null) {
                HBox titleContainer = new HBox();
                titleContainer.getChildren().add(createTitleButton(title));
                titleContainer.getChildren().add(createEditButton());
                listsContainer.getChildren().add(titleContainer);
                title = titles.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TextField createTitleButton(String title) {
        TextField btn = new TextField(title);
        btn.setEditable(false);
        btn.getStyleClass().add("titleButton");
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent actionEvent) {
                ListsController.this.viewScene("overviewScene.fxml", new OverviewController(stage, app, title));
            }
        });
        return btn;
    }

    private Button createEditButton() {
        Button btn = new Button();
        btn.getStyleClass().add("editButton");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var editButton = (Node)actionEvent.getSource();
                var title = (TextField)editButton.getParent().getChildrenUnmodifiable().get(0);
                title.setEditable(true);
            }
        });
        return btn;
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


    @FXML
    void onActionBack() {
        viewScene("menuScene.fxml", new MenuController(stage, app));
    }
}
