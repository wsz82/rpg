package view;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.layer.Layer;
import model.layer.LayersList;

class LayersTableView extends TableView<Layer> {

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
            if (isLevelUnique(newValue)) {
                layer.setLevel(newValue);
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

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);
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
}
