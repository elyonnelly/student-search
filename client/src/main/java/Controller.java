import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Controller {

    @FXML
    Button authorization;

    @FXML
    void onActionAuthorization() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Desktop.getDesktop().browse(new URI("https://vk.com"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
