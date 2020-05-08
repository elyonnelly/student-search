package controllers;

import api.StudentSearchApp;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseRangeController extends Controller implements Initializable {

    private List<List<String>> fileStrings;
    private Controller parentController;
    private List<ListView<String>> pages;

    @FXML
    private
    VBox lineViewComponent;

    @FXML
    private
    Label numberOfPage;

    @FXML
    private
    Label totalPages;

    ChooseRangeController(Controller parentController, Stage stage, StudentSearchApp app, List<List<String>> fileStrings) {
        super(stage, app);
        this.parentController = parentController;
        this.fileStrings = fileStrings;
        pages = new ArrayList<>();
        for (List<String> fileString : fileStrings) {
            pages.add(createPage(fileString));
        }
    }

    @FXML
    void onActionRangesSelected() {
        List<String> selectedItems = new ArrayList<>();
        for(var page : pages) {
            selectedItems.addAll(page.getSelectionModel().getSelectedItems());
        }
        ((SearchController)parentController).handlePdfFile(selectedItems);
    }

    @FXML
    void onActionGoLeft() {
        int number = Integer.parseInt(numberOfPage.getText());
        if (number == 1) {
            return;
        }
        numberOfPage.setText(String.valueOf(number - 1));
        showPage(number - 1);
    }

    @FXML
    void onActionGoRight() {
        int number = Integer.parseInt(numberOfPage.getText());
        if (number == fileStrings.size()) {
            return;
        }
        numberOfPage.setText(String.valueOf(number + 1));
        showPage(number + 1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalPages.setText("/" + fileStrings.size());
        lineViewComponent.getChildren().add(pages.get(0));
    }

    private ListView<String> createPage(List<String> lines) {
        ListView<String> lineView = new ListView<>();
        lineView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lineView.setEditable(false);
        lineView.getItems().addAll(lines);
        return lineView;
    }

    private void showPage(int page) {
        lineViewComponent.getChildren().set(0, pages.get(page - 1));
    }
}
