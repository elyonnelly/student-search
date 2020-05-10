package views;

import api.parse.StatusParticipant;
import controllers.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class CommonView {

    protected Stage stage;

    protected CommonView(Stage stage) {
        this.stage = stage;
    }

    protected void showScene(String scene, Controller controller) {
        var uri = getClass().getClassLoader().getResource(scene);
        FXMLLoader loader = new FXMLLoader(uri);
        loader.setController(controller);
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void showMessage(String message) {
        var alarm = new Alert(Alert.AlertType.INFORMATION, message);
        alarm.show();
    }
}
