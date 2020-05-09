package controllers;

import api.AuthSubscriber;
import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController extends Controller implements AuthSubscriber {

    public AuthController(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    @FXML
    void onActionAuthorization() {
        try {
            app.subscribe(this);
            app.userAuthorization();
        }  catch (IOException e) {
            showMessage(e.getMessage());
        } catch (ApiException | ClientException e) {
            showMessage("Ошибка с подключением к ВКонтакте! Попробуйте позже.");
        }
    }

    @Override
    public void update() {
        showScene("menuScene.fxml", new MenuController(stage, app));
        app.unsubscribe(this);
    }
}
