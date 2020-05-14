package view.content;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.SafeIntegerStringConverter;
import model.content.Content;
import model.location.CurrentLocation;

import java.util.List;

class ContentTableView extends TableView<Content> {

    ContentTableView() {
        super();
        initTable();
    }

    private void initTable() {
        this.setItems(CurrentLocation.get().getContent());
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.getName().equals(oldValue.getName())) {
                this.setItems(CurrentLocation.get().getContent());
            }
        });

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setEditable(true);

        TableColumn<Content, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Content, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Content, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            Content content = t.getTableView().getItems().get(t.getTablePosition().getRow());
            content.setLevel(t.getNewValue());
            levelCol.setVisible(false);
            levelCol.setVisible(true);
        });

        TableColumn<Content, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(param -> param.getValue().visibleProperty());
        visibilityCol.setEditable(true);
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));

        TableColumn<Content, Integer> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(new PropertyValueFactory<>("coords"));

        ObservableList<TableColumn<Content, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
        columns.add(1, typeCol);
        columns.add(2, levelCol);
        columns.add(3, visibilityCol);
        columns.add(4, posCol);
    }

    void removeContents() {
        ObservableList<Content> contentsToRemove = this.getSelectionModel().getSelectedItems();
        CurrentLocation.get().getContent().removeAll(contentsToRemove);
    }

    public void changeVisibility() {
        List<Content> contentsToChange = this.getSelectionModel().getSelectedItems();
        for (Content content : contentsToChange) {
            content.setVisible(!content.isVisible());
        }
    }
}
