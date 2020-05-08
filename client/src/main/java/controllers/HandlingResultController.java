package controllers;

import api.StudentSearchApp;
import api.parse.Query;
import api.parse.StatusParticipant;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HandlingResultController extends Controller implements Initializable {

    Controller parentController;
    private List<Query> queries;

    @FXML
    VBox containerTableData;

    public HandlingResultController(Controller parentController, Stage stage, StudentSearchApp app, List<Query> queries) {
        super(stage, app);
        this.parentController = parentController;
        this.queries = queries;
    }

    @FXML
    void onLoadAnotherFile() {
        ((SearchController)parentController).loadAnotherFile();
    }

    @FXML
    void onStartSearch() {
        parentController.showCurrentStage();
        ((SearchController)parentController).startSearch();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TableView<Query> tableData = new TableView<>(FXCollections.observableList(queries));

        var columnTitles = Arrays.asList("Имя Фамилия", "Город", "Школа", "Класс", "Статус участника");
        var fieldTitles = Arrays.asList("q", "city", "school", "grade", "statusParticipant");

        for (int i = 0; i < 3; i++) {
            var column = new TableColumn<Query, String>(columnTitles.get(i));
            column.setCellValueFactory(new PropertyValueFactory<>(fieldTitles.get(i)));
            tableData.getColumns().add(column);
        }

        var columnGrade = new TableColumn<Query, Integer>(columnTitles.get(3));
        columnGrade.setCellValueFactory(new PropertyValueFactory<>(fieldTitles.get(3)));
        tableData.getColumns().add(columnGrade);

        var column = new TableColumn<Query, StatusParticipant>(columnTitles.get(4));
        column.setCellValueFactory(new PropertyValueFactory<>(fieldTitles.get(4)));
        tableData.getColumns().add(column);

        containerTableData.getChildren().add(tableData);
    }
}
