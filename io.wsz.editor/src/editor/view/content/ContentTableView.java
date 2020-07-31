package editor.view.content;

import editor.view.SafeIntegerStringConverter;
import editor.view.asset.*;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ContentTableView extends TableView<PosItem> {
    private final EditorCanvas editorCanvas;
    private Pointer pointer;

    public ContentTableView(EditorCanvas editorCanvas) {
        super();
        this.editorCanvas = editorCanvas;
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
                return param.getValue().getPos().level;
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
                pi.getPos().level = t.getOldValue();
            } else {
                pi.getPos().level = level;
                editorCanvas.refresh();
            }
            refresh();
        });

        TableColumn<PosItem, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(new PropertyValueFactory<>("visible"));
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));
        visibilityCol.setEditable(true);

        TableColumn<PosItem, Double> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);

        TableColumn<PosItem, Double> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().x;
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        xCol.setOnEditCommit(t -> {
            PosItem pi = t.getTableView().getItems().get(t.getTablePosition().getRow());
            pi.getPos().x = t.getNewValue();
            refresh();
            editorCanvas.refresh();
        });

        TableColumn<PosItem, Double> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().y;
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setOnEditCommit(t -> {
            PosItem pi = t.getTableView().getItems().get(t.getTablePosition().getRow());
            pi.getPos().y = t.getNewValue();
            refresh();
            editorCanvas.refresh();
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
        Coords newPos = pointer.getMark();
        Location location = Controller.get().getCurrentLocation().getLocation();
        for (PosItem pi : itemsToMove) {
            Coords pos = pi.getPos();
            pos.x = newPos.x;
            double y = 0;
            if (newPos.y != 0) {
                y = newPos.y - pi.getImage().getHeight() / Sizes.getMeter();
            }
            pos.y = y;
            pos.setLocation(location);
        }
        refresh();
        editorCanvas.refresh();
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
                    parent, (Creature) pi, true, editorCanvas);
            case TELEPORT -> new TeleportAssetStage(
                    parent, (Teleport) pi, true, editorCanvas);
            case LANDSCAPE -> new LandscapeAssetStage(
                    parent, (Landscape) pi, true, editorCanvas);
            case COVER -> new CoverAssetStage(
                    parent, (Cover) pi, true, editorCanvas);
            case WEAPON -> new WeaponAssetStage(
                    parent, (Weapon) pi, true, editorCanvas);
            case CONTAINER -> new ContainerAssetStage(
                    parent, (Container) pi, true, editorCanvas);
            case INDOOR -> new InDoorAssetStage(
                    parent, (InDoor) pi, true, editorCanvas);
            case OUTDOOR -> new OutDoorAssetStage(
                    parent, (OutDoor) pi, true, editorCanvas);
            case FOG -> null;
        };
        if (itemStage == null) return;
        itemStage.show();
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }
}
