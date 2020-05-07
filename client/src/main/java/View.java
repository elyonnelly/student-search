import api.parse.StatusParticipant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class View {

    Stage stage;

    @FXML
    Label userName;

    @FXML
    ChoiceBox<String> selectFields;

    @FXML
    ListView<String> fieldList;

    @FXML
    TextField loadFileName;

    @FXML
    TextField listTitle;

    @FXML
    ProgressBar progressBar;

    @FXML
    Button stopSearch;

    @FXML
    Button removeField;

    @FXML
    Label progressLabel;

    @FXML
    ScrollPane scrollPaneResult;

    @FXML
    HBox wrapperTableResult;

    @FXML
    Label numberOfResult;

    View(Stage stage) {
        this.stage = stage;
    }

    void viewScene(String scene, Controller controller) {
        var loader = new FXMLLoader(getClass().getResource(scene));
        loader.setController(controller);
        try {
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setName(String name) {
        userName.setText(name);
    }

    void initializeShowResultScene(List<List<Integer>> resultOfSearch) {
        //scrollPaneResult.vvalueProperty().bind(wrapperTableResult.heightProperty());
        int number = 0;
        for (int i = 0; i < 3; i++) {
            if (resultOfSearch.get(i).size() != 0) {
                number += resultOfSearch.get(i).size();
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);

                Label handler = new Label(StatusParticipant.values()[i].getStringValue());
                vBox.getChildren().add(handler);

                ListView<Integer> listView = new ListView<>();
                listView.setItems(FXCollections.observableList(resultOfSearch.get(i)));
                vBox.getChildren().add(listView);

                wrapperTableResult.getChildren().add(vBox);
            }
        }
        numberOfResult.setText(String.valueOf(number));
    }

    void initializeSelectFields() {
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

    void addField(String value) {
        var items = fieldList.getItems();
        if (!value.equals("Другое") && items.contains(value) || !checkNameFields(value)) {
            var alarm = new Alert(Alert.AlertType.ERROR, "Вы уже добавили подобное поле");
            alarm.show();
        }
        else {
            items.add(value);
        }
    }

    void setLoadFileName(String value) {
        loadFileName.setText(value);
    }

    void showMessage(String message) {
        var alarm = new Alert(Alert.AlertType.INFORMATION, message);
        alarm.show();
    }
}
