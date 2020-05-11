package view;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Layer;
import model.LayersList;

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
        levelCol.setOnEditCommit(t -> (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                .setLevel(t.getNewValue())
        );

        TableColumn<Layer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> (t.getTableView().getItems().get(t.getTablePosition().getRow()))
                        .setName(t.getNewValue())
        );

        TableColumn<Layer, Boolean> visibleCol = new TableColumn<>("Visibility");
        visibleCol.setCellValueFactory(param -> param.getValue().getVisibleProperty());
        visibleCol.setEditable(true);
        visibleCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibleCol));

        ObservableList<TableColumn<Layer, ?>> columns = this.getColumns();
        columns.add(0, levelCol);
        columns.add(1, nameCol);
        columns.add(2, visibleCol);
    }

    void remove() {
        ObservableList<Layer> layersToRemove = this.getSelectionModel().getSelectedItems();
        LayersList.get().removeAll(layersToRemove);
    }
}
