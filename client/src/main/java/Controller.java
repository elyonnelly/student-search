import api.StudentSearchApp;
import javafx.stage.Stage;

public class Controller extends View {

    final StudentSearchApp app;

    Controller(Stage stage, StudentSearchApp app) {
        super(stage);
        this.app = app;
    }
}
