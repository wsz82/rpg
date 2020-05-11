package view;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.assets.Assets;
import model.assets.AssetsList;
import model.items.ItemType;

public class AssetsTableView extends TableView<Assets> {
    private final ItemType itemType;

    AssetsTableView(ItemType itemType) {
        super();
        this.itemType = itemType;
        initTable();
        setFilteredItems();
    }

    private void initTable() {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TableColumn<Assets, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<TableColumn<Assets, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
    }

    private void setFilteredItems() {
        ObservableList<Assets> assets = AssetsList.get();
        FilteredList<Assets> filteredList = new FilteredList<Assets>(assets);
        filteredList.setPredicate(asset -> asset.getType() == itemType);
        this.setItems(filteredList);
    }


}
