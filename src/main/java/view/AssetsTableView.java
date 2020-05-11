package view;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.assets.Asset;
import model.assets.AssetsList;
import model.items.ItemType;

public class AssetsTableView extends AssetsGenericTableView {
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem addAsset = new MenuItem("Add asset");
    private final MenuItem removeAsset = new MenuItem("Remove assets");
    private final ItemType itemType;

    AssetsTableView(ItemType itemType) {
        super();
        this.itemType = itemType;
        initTable();
        setFilteredItems();
        setUpContextMenu();
    }

    private void initTable() {
        TableColumn<Asset, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        ObservableList<TableColumn<Asset, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
    }

    private void setFilteredItems() {
        ObservableList<Asset> assets = AssetsList.get();
        FilteredList<Asset> filteredList = new FilteredList<Asset>(assets);
        filteredList.setPredicate(asset -> asset.getType() == itemType);
        this.setItems(filteredList);
    }

    private void setUpContextMenu() {
        addAsset.setOnAction(event -> addAsset());
        removeAsset.setOnAction(event -> removeAssets());
        contextMenu.getItems().addAll(addAsset, removeAsset);
        this.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    void addAsset() {
        Asset asset = new Asset("Name", itemType, "D:");
        AssetsList.get().add(asset);
    }

    void removeAssets() {
        ObservableList<Asset> assetsToRemove = this.getSelectionModel().getSelectedItems();
        AssetsList.get().removeAll(assetsToRemove);
    }
}
