package view.layers;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.SafeIntegerStringConverter;
import model.content.ContentList;
import model.layer.Layer;
import model.layer.LayersList;
import model.stage.CurrentLayer;

import java.util.HashSet;
import java.util.Set;

class LayersTableView extends TableView<Layer> implements LevelValueObservable, VisibleValueObservable {
    private final Set<LevelValueListener> levelValueListeners = new HashSet<>();
    private final Set<VisibleValueListener> visibleValueListeners = new HashSet<>();

    LayersTableView() {
        super();
        initTable();
    }

    private void initTable() {
        this.setItems(LayersList.get());

        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setEditable(true);

        TableColumn<Layer, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            Layer layer = t.getTableView().getItems().get(t.getTablePosition().getRow());
            int newValue = t.getNewValue();

            notifyLevelValueListeners(t.getOldValue(), t.getNewValue());

            if (isLevelUnique(newValue)) {
                layer.setLevel(newValue);
                CurrentLayer.setCurrentLayer(newValue);
            } else {
                layer.setLevel(t.getOldValue());
            }
            levelCol.setVisible(false);
            levelCol.setVisible(true);
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
            nameCol.setVisible(false);
            nameCol.setVisible(true);
        });

        TableColumn<Layer, Boolean> visibleCol = new TableColumn<>("Visibility");
        visibleCol.setCellValueFactory(param -> param.getValue().getVisibleProperty());
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

        this.attachLevelValueListener(ContentList.getInstance());
        this.attachVisibleValueListener(ContentList.getInstance());
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldSel, newSel) -> {
            if (newSel != null) {
                CurrentLayer.setCurrentLayer(newSel.getLevel());
            }
        });
    }

    private boolean isNameUnique(String newValue) {
        return this.getItems().stream()
                .noneMatch(layer -> layer.getName().equals(newValue));
    }

    private boolean isLevelUnique(int newValue) {
        return this.getItems().stream()
                .noneMatch(layer -> layer.getLevel() == newValue);
    }

    void removeLayers() {
        ObservableList<Layer> layersToRemove = this.getSelectionModel().getSelectedItems();
        LayersList.get().removeAll(layersToRemove);
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
