import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.scene.paint.Color;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("searchScene.fxml"));
        primaryStage.setTitle("StudentSearch");
        var scene = new Scene(root);
        scene.setFill(Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(600);
        primaryStage.setMaxWidth(750);
        primaryStage.setMaxHeight(600);
        primaryStage.resizableProperty().setValue(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
