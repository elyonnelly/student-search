import api.StudentSearchApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Контроллер сцены с отображением результатов поиска
 */
public class ResultController extends Controller implements Initializable {

    private List<List<Integer>> results;
    private String listTitle;

    ResultController(Stage stage, StudentSearchApp app, List<List<Integer>> results, String listTitle) {
        super(stage, app);
        this.results = results;
        this.listTitle = listTitle;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeShowResultScene(results);
    }

    @FXML
    void onActionDownloadFiles() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        try {
            app.saveFile(results, listTitle, selectedDirectory.getPath() + "\\");
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Не удается сохранить файлы!");
        }
        showMessage("Файлы сохранены!");
    }
}
