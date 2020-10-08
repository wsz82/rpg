package editor.view.layer;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import editor.view.location.CurrentObservableLocation;
import editor.view.stage.EditorCanvas;
import editor.view.utilities.BooleanGetter;
import editor.view.utilities.BooleanSetter;
import editor.view.utilities.CheckBoxCallback;
import editor.view.utilities.SafeIntegerStringConverter;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;
import java.util.stream.Collectors;

class LayersTableView extends TableView<Layer> {
    private final ContentTableView contentTableView;
    private final EditorCanvas editorCanvas;
    private final EditorController controller;
    private final BooleanGetter<Layer> visibleGetter;
    private final BooleanSetter<Layer> visibleSetter;

    public LayersTableView(ContentTableView contentTableView, EditorCanvas editorCanvas, EditorController controller) {
        super();
        this.contentTableView = contentTableView;
        this.editorCanvas = editorCanvas;
        this.controller = controller;
        visibleGetter = Layer::isVisible;
        visibleSetter = (layer, value) -> {
            int level = layer.getLevel();
            layer.setVisible(value);
            updateItemsVisibility(level, value);
            contentTableView.refresh();
            editorCanvas.refresh();
        };
    }

    public void initTable() {
        CurrentObservableLocation currentObservableLocation = controller.getCurrentObservableLocation();
        ObservableList<Layer> layers = currentObservableLocation.getLayers();
        setItems(layers);

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Layer, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().getLevel();
            }
        });
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            Layer layer = getSelectionModel().getSelectedItem();
            int newLevel = t.getNewValue();
            Integer oldLevel = t.getOldValue();
            updateItemsLevel(newLevel, oldLevel);

            if (isLevelUnique(newLevel)) {
                layer.setLevel(newLevel);
                controller.getCurrentObservableLayer().setLayer(layer);
                contentTableView.refresh();
                editorCanvas.refresh();
            } else {
                layer.setLevel(oldLevel);
            }
            refresh();
        });

        TableColumn<Layer, String> nameCol = new TableColumn<>("ID");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getId();
            }
        });
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            Layer layer = getSelectionModel().getSelectedItem();
            String newValue = t.getNewValue();
            if (isNameUnique(newValue)) {
                layer.setId(newValue);
            } else {
                layer.setId(t.getOldValue());
            }
            refresh();
        });
        nameCol.setPrefWidth(100);

        TableColumn<Layer, CheckBox> visibleCol = new TableColumn<>("Visibility");
        visibleCol.setEditable(true);
        visibleCol.setCellValueFactory(new CheckBoxCallback<>(visibleGetter, visibleSetter));

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);

        getSelectionModel().selectedItemProperty().addListener((observable, oldSel, newSel) -> {
            if (newSel != null) {
                controller.getCurrentObservableLayer().setLayer(newSel);
            }
        });
    }

    private void updateItemsLevel(int newLevel, Integer oldLevel) {
        for (PosItem<?,?> pi : controller.getCurrentObservableLocation().getItemsList().getMergedList()) {
            Coords pos = pi.getPos();
            if (pos.level == oldLevel) {
                pos.level = newLevel;
            }
        }
    }

    private void updateItemsVisibility(int level, boolean visible) {
        for (PosItem<?,?> pi : controller.getCurrentObservableLocation().getItemsList().getMergedList()) {
            if (pi.getPos().level == level) {
                pi.setVisible(visible);
            }
        }
    }

    private boolean isNameUnique(String newValue) {
        return getItems().stream()
                .noneMatch(layer -> layer.getId().equals(newValue));
    }

    private boolean isLevelUnique(int newValue) {
        return getItems().stream()
                .noneMatch(layer -> layer.getLevel() == newValue);
    }

    void removeLayers() {
        List<Layer> layersToRemove = getSelectionModel().getSelectedItems();
        List<Layer> layers = controller.getCurrentObservableLocation().getLayers();
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
                .map(Layer::getLevel)
                .collect(Collectors.toList());
        List<PosItem<?,?>> itemsToRemove = controller.getCurrentObservableLocation().getItemsList().getMergedList().stream()
                .filter(pi -> levelsToRemove.contains(pi.getPos().level))
                .collect(Collectors.toList());
        controller.getCurrentObservableLocation().getItemsList().removeAll(itemsToRemove);
    }

    public void changeVisibility() {
        List<Layer> layersToChange = getSelectionModel().getSelectedItems();
        for (Layer layer : layersToChange) {
            layer.setVisible(!layer.isVisible());
        }
    }
}
