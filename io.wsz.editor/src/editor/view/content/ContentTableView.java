package editor.view.content;

import editor.view.SafeIntegerStringConverter;
import editor.view.asset.*;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ContentTableView extends TableView<PosItem> {
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
        setItems(CurrentLocation.get().getItems());
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            setItems(newValue.getItems().get());
        });

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<PosItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getName();
            }
        });
        TableColumn<PosItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getType().toString();
            }
        });
        TableColumn<PosItem, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getLevel();
            }
        });
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            int level = t.getNewValue();
            PosItem pi = t.getTableView().getItems().get(t.getTablePosition().getRow());

            List<Integer> levels = Controller.get().getCurrentLocation().getLayers().stream()
                    .map(l -> l.getLevel())
                    .collect(Collectors.toList());
            if (!levels.contains(level)) {
                alertLayerNotExisting(level);
                pi.setLevel(t.getOldValue());
            } else {
                pi.setLevel(level);
                EditorCanvas.get().refresh();
            }
            refresh();
        });

        TableColumn<PosItem, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(new PropertyValueFactory<>("visible"));
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));
        visibilityCol.setEditable(true);

        TableColumn<PosItem, Integer> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);

        TableColumn<PosItem, Integer> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getPos().x;
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        xCol.setOnEditCommit(t -> {
            PosItem pi = t.getTableView().getItems().get(t.getTablePosition().getRow());
            pi.getPos().x = t.getNewValue();
            refresh();
            EditorCanvas.get().refresh();
        });

        TableColumn<PosItem, Integer> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getPos().y;
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        yCol.setOnEditCommit(t -> {
            PosItem pi = t.getTableView().getItems().get(t.getTablePosition().getRow());
            pi.getPos().y = t.getNewValue();
            refresh();
            EditorCanvas.get().refresh();
        });
        posCol.getColumns().addAll(xCol, yCol);

        ObservableList<TableColumn<PosItem, ?>> columns = getColumns();
        columns.add(0, nameCol);
        columns.add(1, typeCol);
        columns.add(2, levelCol);
        columns.add(3, visibilityCol);
        columns.add(4, posCol);
    }

    private void alertLayerNotExisting(int level) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION, "Layer " + level + " does not exist!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    void removeContents() {
        ObservableList<PosItem> itemsToRemove = getSelectionModel().getSelectedItems();
        CurrentLocation.get().getItems().removeAll(itemsToRemove);
    }

    public void changeVisibility() {
        List<PosItem> itemsToChange = getSelectionModel().getSelectedItems();
        for (PosItem pi : itemsToChange) {
            pi.setVisible(!pi.getVisible());
        }
    }

    public void moveToPointer() {
        List<PosItem> itemsToMove = getSelectionModel().getSelectedItems();
        Coords newPos = Pointer.get().getMark();
        for (PosItem pi : itemsToMove) {
            pi.getPos().x = newPos.x;
            int y = 0;
            if (newPos.y != 0) {
                y = newPos.y - (int) pi.getImage().getHeight();
            }
            pi.getPos().y = y;
        }
        refresh();
        EditorCanvas.get().refresh();
    }

    public void editItem(Stage parent) {
        PosItem pi = getSelectionModel().getSelectedItem();
        openEditWindow(parent, pi);
    }

    public void openEditWindow(Stage parent, PosItem pi) {
        if (pi == null) {
            return;
        }
        ItemType type = pi.getType();
        AssetStage itemStage = switch (type) {
            case CREATURE -> new CreatureAssetStage(
                    parent, (Creature) pi, true);
            case TELEPORT -> new TeleportAssetStage(
                    parent, (Teleport) pi, true);
            case LANDSCAPE -> new LandscapeAssetStage(
                    parent, (Landscape) pi, true);
            case COVER -> new CoverAssetStage(
                    parent, (Cover) pi, true);
        };
        itemStage.show();
    }
}
