package editor.view.content;

import editor.view.SafeIntegerStringConverter;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.stream.Collectors;

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
        nameCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getItem().getAsset().getName();
            }
        });
        TableColumn<Content, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getItem().getAsset().getType().toString();
            }
        });
        TableColumn<Content, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getItem().getLevel();
            }
        });
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            int level = t.getNewValue();
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());

            List<Integer> levels = Controller.get().getCurrentLocation().getLayers().stream()
                    .map(l -> l.getLevel())
                    .collect(Collectors.toList());
            if (!levels.contains(level)) {
                alertLayerNotExisting(level);
                c.getItem().setLevel(t.getOldValue());
            } else {
                c.setLevel(level);
                int z = getUniqueZ(c.getItem().getPos().getZ(), level);
                c.getItem().getPos().setZ(z);
            }
            refresh();
        });

        TableColumn<Content, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(param -> param.getValue().visibleProperty());
        visibilityCol.setEditable(true);
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));

        TableColumn<Content, Double> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);

        TableColumn<Content, Double> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getItem().getPos().getX();
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        xCol.setOnEditCommit(t -> {
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());
            c.getItem().getPos().setX(t.getNewValue());
            refresh();
        });

        TableColumn<Content, Double> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getItem().getPos().getY();
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setOnEditCommit(t -> {
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());
            c.getItem().getPos().setY(t.getNewValue());
            refresh();
        });

        TableColumn<Content, Integer> zCol = new TableColumn<>("Z");
        zCol.setEditable(true);
        zCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getItem().getPos().getZ();
            }
        });
        zCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        zCol.setOnEditCommit(t -> {
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());
            reverseZwithOtherContent(c, t.getOldValue(), t.getNewValue());
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

    private void alertLayerNotExisting(int level) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Layer " + level + " does not exist!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private int getUniqueZ(int z, int destLevel) {
        List<Integer> zPositions = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getLevel() == destLevel)
                .map(c -> c.getItem().getPos().getZ())
                .collect(Collectors.toList());
        return iterateForUniqueZ(z, zPositions);
    }

    private int iterateForUniqueZ(int z, List<Integer> zPositions) {
        if (!zPositions.contains(z)) {
            return z;
        } else {
            z += 1;
            return iterateForUniqueZ(z, zPositions);
        }
    }

    private void reverseZwithOtherContent(Content c1, int oldValue, int newValue) {
        List<Content> singleContent = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getPos().getZ() == newValue)
                .collect(Collectors.toList());
        if (!singleContent.isEmpty()) {
            Content c2 = singleContent.get(0);
            c2.getItem().getPos().setZ(oldValue);
        }
        c1.getItem().getPos().setZ(newValue);
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

    public void moveToPointer() {
        List<Content> contentsToMove = getSelectionModel().getSelectedItems();
        Coords newPos = Pointer.getMark();
        for (Content c : contentsToMove) {
            c.getItem().getPos().setX(newPos.getX());
            c.getItem().getPos().setY(newPos.getY());
        }
        refresh();
    }
}
