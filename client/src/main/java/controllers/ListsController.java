package controllers;

import api.StudentSearchApp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ListsController extends Controller implements Initializable {

    @FXML
    VBox listsContainer;

    @FXML
    Label userName;

    public ListsController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userName.setText(app.getUserName());
        //открываем файл
        try (BufferedReader titles = new BufferedReader(new FileReader("listTitles.txt"))) {
            String title = titles.readLine();
            while(title != null) {
                HBox titleContainer = new HBox();
                titleContainer.getChildren().add(createTitleButton(title));
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
                ListsController.this.showScene("overviewScene.fxml", new OverviewController(stage, app, title));
            }
        });
        return btn;
    }


    @FXML
    void onActionBack() {
        showScene("menuScene.fxml", new MenuController(stage, app));
    }
}
