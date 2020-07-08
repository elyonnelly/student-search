package views;

import api.StudentSearchApp;
import api.parse.Query;
import controllers.Controller;
import controllers.HandlingResultController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.swing.text.TableView;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SearchView extends Controller {

    Stage childrenStage;

    //region FXML fields
    @FXML
    protected Label userName;
    @FXML
    protected ListView<String> selectFields;

    @FXML
    protected ListView<String> fieldList;

    @FXML
    protected TextField loadFileName;

    @FXML
    protected TextField listTitle;

    @FXML
    protected ProgressBar progressBar;

    @FXML
    protected Button stopSearch;

    @FXML
    protected Button removeField;

    @FXML
    protected Label progressLabel;

    @FXML
    protected CheckBox append;

    @FXML
    protected Button showResult;

    @FXML
    protected TableView tableParsedData;

    protected SearchView(Stage stage, StudentSearchApp app) {
        super(stage, app);
    }

    protected void showHandlingResult(List<Query> queries) {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setMinHeight(this.stage.getMinHeight());
        stage.setMinWidth(this.stage.getMinWidth());
        showStage(stage, "Результат обработки файла", "handlingResultScene.fxml", new HandlingResultController(this, stage, app, queries));
    }

    protected void closeChildrenStage() {
        if (childrenStage != null && childrenStage.isShowing()) {
            childrenStage.close();
        }
    }

    protected void showStage(Stage newStage, String title, String sceneName, Controller controller) {
        if (controller == this) {
            childrenStage = newStage;
        }
        newStage.setTitle(title);
        var loader = new FXMLLoader(getClass().getClassLoader().getResource(sceneName));
        loader.setController(controller);
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        newStage.setScene(scene);
        newStage.show();
    }

    protected void blockButtons(boolean state) {
        progressBar.setDisable(!state);
        stopSearch.setDisable(!state);
        selectFields.setDisable(state);
        removeField.setDisable(state);
        listTitle.setDisable(state);
    }

    protected void setName(String name) {
        userName.setText(name);
    }

    protected void initializeSelectFields() {
        ObservableList<String> fields = FXCollections.observableArrayList("Фамилия Имя (Отчество)", "Фамилия И. (О.)", "Фамилия", "Имя",
                "Город", "Школа", "Класс", "Статус участника", "Другое");
        selectFields.setItems(fields);
    }

    private boolean checkNameFields(String value) {
        var surnameFields = Arrays.asList("Фамилия Имя (Отчество)", "Фамилия И. (О.)", "Фамилия", "Имя");
        if (surnameFields.contains(value)) {
            for (var field : fieldList.getItems()) {
                if (surnameFields.contains(field)
                        && !(value.equals("Фамилия") && field.equals("Имя") ||value.equals("Имя") && field.equals("Фамилия"))) {
                    return false;
                }
            }
        }
        return true;
    }


    protected void addField(String value) {
        if (value == null) {
            return;
        }
        var items = fieldList.getItems();
        if (!value.equals("Другое") && items.contains(value) || !checkNameFields(value)) {
            showMessage("Вы уже добавили подобное поле");
        }
        else {
            items.add(value);
        }
    }

    protected void setLoadFileName(String value) {
        loadFileName.setText(value);
    }

}
