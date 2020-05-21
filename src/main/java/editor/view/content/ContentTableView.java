package editor.view.content;

import editor.view.SafeIntegerStringConverter;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import model.content.Content;
import model.location.CurrentLocation;

import java.util.List;

public class ContentTableView extends TableView<Content> {
    private static ContentTableView singleton;

    public static ContentTableView get() {
        if (singleton == null) {
            singleton = new ContentTableView();
        }
        return singleton;
    }

    private ContentTableView() {
        super();
        initTable();
    }

    private void initTable() {
        setItems(CurrentLocation.get().getContent());
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            setItems(newValue.getContents().get());
        });

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

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
            refresh();
        });

        TableColumn<Content, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(param -> param.getValue().visibleProperty());
        visibilityCol.setEditable(true);
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));

        TableColumn<Content, Double> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);
        posCol.setCellValueFactory(new PropertyValueFactory<>("coords"));

        TableColumn<Content, Double> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().getX();
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        xCol.setOnEditCommit(t -> {
            Content content = t.getTableView().getItems().get(t.getTablePosition().getRow());
            content.getPos().setX(t.getNewValue());
            refresh();
        });

        TableColumn<Content, Double> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().getY();
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setOnEditCommit(t -> {
            Content content = t.getTableView().getItems().get(t.getTablePosition().getRow());
            content.getPos().setY(t.getNewValue());
            refresh();
        });

        TableColumn<Content, Integer> zCol = new TableColumn<>("Z");
        zCol.setEditable(true);
        zCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getPos().getZ();
            }
        });
        zCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        zCol.setOnEditCommit(t -> {
            Content content = t.getTableView().getItems().get(t.getTablePosition().getRow());
            content.getPos().setZ(t.getNewValue());
            refresh();
        });
        posCol.getColumns().addAll(xCol, yCol, zCol);

        ObservableList<TableColumn<Content, ?>> columns = getColumns();
        columns.add(0, nameCol);
        columns.add(1, typeCol);
        columns.add(2, levelCol);
        columns.add(3, visibilityCol);
        columns.add(4, posCol);
    }

    void removeContents() {
        ObservableList<Content> contentsToRemove = getSelectionModel().getSelectedItems();
        CurrentLocation.get().getContent().removeAll(contentsToRemove);
    }

    public void changeVisibility() {
        List<Content> contentsToChange = getSelectionModel().getSelectedItems();
        for (Content content : contentsToChange) {
            content.setVisible(!content.isVisible());
        }
    }
}
