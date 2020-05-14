package view.assets;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.assets.Asset;
import model.assets.AssetToContentConverter;
import model.assets.AssetsList;
import model.content.Content;
import model.items.ItemType;
import model.location.CurrentLocation;
import model.stage.Coordinates;
import model.stage.Pointer;

import java.util.List;

class AssetsTableView extends AssetsGenericTableView {
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem addAsset = new MenuItem("Add asset");
    private final MenuItem removeAsset = new MenuItem("Remove asset/s");
    private final MenuItem addItemsToStage = new MenuItem("Add item/s to stage");
    private final ItemType itemType;
    private final Stage parent;

    AssetsTableView(Stage parent, ItemType itemType) {
        super();
        this.itemType = itemType;
        this.parent = parent;
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
        FilteredList<Asset> filteredList = new FilteredList<>(assets);
        filteredList.setPredicate(asset -> asset.getType() == itemType);
        this.setItems(filteredList);
    }

    private void setUpContextMenu() {
        addAsset.setOnAction(event -> addAsset());
        removeAsset.setOnAction(event -> removeAssets());
        addItemsToStage.setOnAction(event -> {
            Coordinates pointerCoordinates = Pointer.getMark();
            addItemsToStageAndContents(pointerCoordinates);
        });
        contextMenu.getItems().addAll(addAsset, removeAsset, addItemsToStage);
        this.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    void addItemsToStageAndContents(Coordinates pos) {
        List<Asset> selectedAssets = this.getSelectionModel().getSelectedItems();
        List<Content> contents = AssetToContentConverter.convert(selectedAssets, pos);
        CurrentLocation.get().getContent().addAll(contents);
    }

    void addAsset() {
        NewAssetStage newAssetStage = new NewAssetStage(parent, itemType);
        newAssetStage.show();
    }

    void removeAssets() {
        ObservableList<Asset> assetsToRemove = this.getSelectionModel().getSelectedItems();
        AssetsList.get().removeAll(assetsToRemove);
    }
}
