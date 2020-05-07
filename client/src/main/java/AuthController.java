import api.AuthSubscriber;
import api.StudentSearchApp;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController extends Controller  implements AuthSubscriber {

    public AuthController(Stage stage, StudentSearchApp app) {
        super(stage, app);
        try {
            app.startApp();
        } catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.show();
        }
    }

    @FXML
    void onActionAuthorization() {
        try {
            app.subscribe(this);
            app.userAuthorization();
        }  catch (IOException e) {
            var alert = new Alert(Alert.AlertType.ERROR, e.getMessage());
            alert.showAndWait();
        } catch (ApiException | ClientException e) {
            var alert = new Alert(Alert.AlertType.ERROR, "Ошибка с подключением к ВКонтакте! Попробуйте позже.");
            alert.showAndWait();
        }
    }

    @Override
    public void update() {
        viewScene("menuScene.fxml", new MenuController(stage, app));
        app.unsubscribe(this);
    }
}
