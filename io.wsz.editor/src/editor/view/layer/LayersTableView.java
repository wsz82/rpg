package editor.view.layer;

import editor.view.SafeIntegerStringConverter;
import editor.view.content.ContentTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.content.LevelValueListener;
import io.wsz.model.content.LevelValueObservable;
import io.wsz.model.content.VisibleValueListener;
import io.wsz.model.content.VisibleValueObservable;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class LayersTableView extends TableView<Layer> implements LevelValueObservable, VisibleValueObservable {
    private final Set<LevelValueListener> levelValueListeners = new HashSet<>();
    private final Set<VisibleValueListener> visibleValueListeners = new HashSet<>();
    private final ContentTableView contentTableView;
    private final EditorCanvas editorCanvas;

    LayersTableView(ContentTableView contentTableView, EditorCanvas editorCanvas) {
        super();
        this.contentTableView = contentTableView;
        this.editorCanvas = editorCanvas;
        initTable();
    }

    private void initTable() {
        setItems(CurrentLocation.get().getLayers());
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            setItems(newValue.getLayers().get());
        });

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Layer, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            Layer layer = t.getTableView().getItems().get(t.getTablePosition().getRow());
            int level = t.getNewValue();
            notifyLevelValueListeners(t.getOldValue(), level);

            if (isLevelUnique(level)) {
                layer.setLevel(level);
                Controller.get().getCurrentLayer().setLayer(layer);
                contentTableView.refresh();
                editorCanvas.refresh();
            } else {
                layer.setLevel(t.getOldValue());
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
            notifyVisibleValueListeners(p.getValue().getLevel(), p.getValue().getVisible());
            return p.getValue().getVisibleProperty();
        });

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);

        attachLevelValueListener(CurrentLocation.get().getItemsList());
        attachVisibleValueListener(CurrentLocation.get().getItemsList());
        getSelectionModel().selectedItemProperty().addListener((observable, oldSel, newSel) -> {
            if (newSel != null) {
                Controller.get().getCurrentLayer().setLayer(newSel);
            }
        });
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
        List<Layer> layers = Controller.get().getCurrentLocation().getLayers();
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
        List<PosItem> itemsToRemove = Controller.get().getCurrentLocation().getItems().stream()
                .filter(pi -> levelsToRemove.contains(pi.getPos().level))
                .collect(Collectors.toList());
        Controller.get().getCurrentLocation().getItems().removeAll(itemsToRemove);
    }

    public void changeVisibility() {
        List<Layer> layersToChange = getSelectionModel().getSelectedItems();
        for (Layer layer : layersToChange) {
            layer.setVisible(!layer.getVisible());
        }
    }

    @Override
    public void attachLevelValueListener(LevelValueListener listener) {
        levelValueListeners.add(listener);
    }

    @Override
    public void removeLevelValueListener(LevelValueListener listener) {
        levelValueListeners.remove(listener);
    }

    @Override
    public void notifyLevelValueListeners(int oldValue, int newValue) {
        levelValueListeners.forEach(listener -> listener.onLevelValueChanged(oldValue, newValue));
    }

    @Override
    public void attachVisibleValueListener(VisibleValueListener listener) {
        visibleValueListeners.add(listener);
    }

    @Override
    public void removeVisibleValueListener(VisibleValueListener listener) {
        visibleValueListeners.remove(listener);
    }

    @Override
    public void notifyVisibleValueListeners(int level, boolean newValue) {
        visibleValueListeners.forEach(listener -> listener.onVisibleValueChanged(level, newValue));
    }
}
