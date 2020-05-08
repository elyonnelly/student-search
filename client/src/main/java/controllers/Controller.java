package controllers;

import api.StudentSearchApp;
import javafx.stage.Stage;
import views.CommonView;

public class Controller extends CommonView {

    protected final StudentSearchApp app;

    public Controller(Stage stage, StudentSearchApp app) {
        super(stage);
        this.app = app;
    }

    public void showCurrentStage() {
        stage.toFront();
    }
}
