package editor.view.layer;

import editor.model.EditorController;
import editor.view.SafeIntegerStringConverter;
import editor.view.content.ContentTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;
import java.util.stream.Collectors;

class LayersTableView extends TableView<Layer> {
    private final ContentTableView contentTableView;
    private final EditorCanvas editorCanvas;
    private final EditorController editorController;
    private final Controller controller;

    LayersTableView(ContentTableView contentTableView, EditorCanvas editorCanvas, EditorController editorController) {
        super();
        this.contentTableView = contentTableView;
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
        controller = editorController.getController();
        initTable();
    }

    private void initTable() {
        ObservableList<Layer> layers = controller.getCurrentLocation().getLayers();
        setItems(layers);
        controller.getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            setItems(newValue.getLayers());
        });

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Layer, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            Layer layer = t.getTableView().getItems().get(t.getTablePosition().getRow());
            int newLevel = t.getNewValue();
            Integer oldLevel = t.getOldValue();
            updateItemsLevel(newLevel, oldLevel);

            if (isLevelUnique(newLevel)) {
                layer.setLevel(newLevel);
                controller.getCurrentLayer().setLayer(layer);
                contentTableView.refresh();
                editorCanvas.refresh();
            } else {
                layer.setLevel(oldLevel);
            }
            refresh();
        });

        TableColumn<Layer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            Layer layer = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String newValue = t.getNewValue();
            if (isNameUnique(newValue)) {
                layer.setName(newValue);
            } else {
                layer.setName(t.getOldValue());
            }
            refresh();
        });
        nameCol.setPrefWidth(100);

        TableColumn<Layer, Boolean> visibleCol = new TableColumn<>("Visibility");
        visibleCol.setEditable(true);
        visibleCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibleCol));
        visibleCol.setCellValueFactory(p -> {
            Layer newLayer = p.getValue();
            int level = newLayer.getLevel();
            boolean visible = newLayer.getVisible();
            updateItemsVisibility(level, visible);
            return newLayer.getVisibleProperty();
        });

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);

        getSelectionModel().selectedItemProperty().addListener((observable, oldSel, newSel) -> {
            if (newSel != null) {
                controller.getCurrentLayer().setLayer(newSel);
            }
        });
    }

    private void updateItemsLevel(int newLevel, Integer oldLevel) {
        for (PosItem pi : controller.getCurrentLocation().getItems()) {
            Coords pos = pi.getPos();
            if (pos.level == oldLevel) {
                pos.level = newLevel;
            }
        }
    }

    private void updateItemsVisibility(int level, boolean visible) {
        for (PosItem pi : controller.getCurrentLocation().getItems()) {
            if (pi.getPos().level == level) {
                pi.setIsVisible(visible);
            }
        }
    }

    private boolean isNameUnique(String newValue) {
        return getItems().stream()
                .noneMatch(layer -> layer.getName().equals(newValue));
    }

    private boolean isLevelUnique(int newValue) {
        return getItems().stream()
                .noneMatch(layer -> layer.getLevel() == newValue);
    }

    void removeLayers() {
        List<Layer> layersToRemove = getSelectionModel().getSelectedItems();
        List<Layer> layers = controller.getCurrentLocation().getLayers();
        boolean listSizesAreEqual = layers.size() == layersToRemove.size();
        if (listSizesAreEqual) {
            layersToRemove = layersToRemove.stream()
                    .limit(layers.size() - 1)
                    .collect(Collectors.toList());
        }
        removeContentsFromLayers(layersToRemove);
        layers.removeAll(layersToRemove);
    }

    private void removeContentsFromLayers(List<Layer> layersToRemove) {
        List<Integer> levelsToRemove = layersToRemove.stream()
                .map(l -> l.getLevel())
                .collect(Collectors.toList());
        List<PosItem> itemsToRemove = controller.getCurrentLocation().getItems().stream()
                .filter(pi -> levelsToRemove.contains(pi.getPos().level))
                .collect(Collectors.toList());
        controller.getCurrentLocation().getItems().removeAll(itemsToRemove);
    }

    public void changeVisibility() {
        List<Layer> layersToChange = getSelectionModel().getSelectedItems();
        for (Layer layer : layersToChange) {
            layer.setVisible(!layer.getVisible());
        }
    }
}
