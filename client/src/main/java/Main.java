import api.StudentSearchApp;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        var loader = new FXMLLoader(getClass().getClassLoader().getResource("authScene.fxml"));
        primaryStage.setTitle("StudentSearch");
        try {
            loader.setController(new controllers.AuthController(primaryStage,
                    new StudentSearchApp(getClass().getClassLoader().getResourceAsStream("cities.txt"),
                                        getClass().getClassLoader().getResourceAsStream("config.properties"))));
            var scene = new Scene(loader.load());
            scene.setFill(Color.WHITE);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        }
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
