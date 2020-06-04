package editor.view.asset;

import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.Asset;
import io.wsz.model.item.AssetConverter;
import io.wsz.model.item.AssetsList;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

class AssetsTableView extends AssetsGenericTableView {
    private final ItemType itemType;
    private final Stage parent;

    AssetsTableView(Stage parent, ItemType itemType) {
        super();
        this.itemType = itemType;
        this.parent = parent;
        initTable();
        setFilteredItems();
        setUpContextMenu();
        setEditable(true);
    }

    private void initTable() {
        TableColumn<Asset, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            Asset asset = t.getTableView().getItems().get(t.getTablePosition().getRow());
            String newValue = t.getNewValue();
            if (assetNameIsNotUnique(newValue)) {
                asset.setName(t.getOldValue());
            } else {
                asset.setName(newValue);
            }
            refresh();
        });

        TableColumn<Asset, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("relativePath"));
        pathCol.setEditable(false);
        pathCol.setCellFactory(TextFieldTableCell.forTableColumn());

        ObservableList<TableColumn<Asset, ?>> columns = getColumns();
        columns.addAll(nameCol, pathCol);
    }

    private boolean assetNameIsNotUnique(String newValue) {
        return Controller.get().getAssetsList().stream()
                .anyMatch(p -> p.getName().equals(newValue));
    }

    private void setFilteredItems() {
        ObservableList<Asset> assets = AssetsList.get();
        FilteredList<Asset> filteredList = new FilteredList<>(assets);
        filteredList.setPredicate(asset -> asset.getType() == itemType);
        setItems(filteredList);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem addAsset = new MenuItem("Add asset");
        final MenuItem editAsset = new MenuItem("Edit asset");
        final MenuItem removeAsset = new MenuItem("Remove asset/s");
        final MenuItem addItemsToStage = new MenuItem("Add item/s to stage");
        addAsset.setOnAction(event -> addAsset());
        editAsset.setOnAction(event -> editAsset());
        removeAsset.setOnAction(event -> removeAssets());
        addItemsToStage.setOnAction(event -> {
            Coords mark = Pointer.get().getMark();
            addItemsToStageAndContents(mark);
        });
        contextMenu.getItems().addAll(addAsset, editAsset, removeAsset, addItemsToStage);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void editAsset() {
        Asset assetToEdit = getSelectionModel().getSelectedItem();
        if (assetToEdit == null) {
            return;
        }
        AssetStage assetStage = switch (itemType) {
            case CREATURE -> new CreatureAssetStage(parent, assetToEdit);
            case TELEPORT -> new TeleportAssetStage(parent, assetToEdit);
            default -> new AssetStageImpl(parent, assetToEdit);
        };
        assetStage.show();
    }

    void addItemsToStageAndContents(Coords pos) {
        List<Asset> selectedAssets = getSelectionModel().getSelectedItems();
        List<Content> contents = AssetConverter.convertToContent(selectedAssets, pos);
        Controller.get().getCurrentLocation().getContent().addAll(contents);
    }

    void addAsset() {
        AssetStage assetStage = switch (itemType) {
            case CREATURE -> new CreatureAssetStage(parent, itemType);
            case TELEPORT -> new TeleportAssetStage(parent, itemType);
            default -> new AssetStageImpl(parent, itemType);
        };
        assetStage.show();
    }

    void removeAssets() {
        List<Asset> assetsToRemove = getSelectionModel().getSelectedItems();
        removeContent(assetsToRemove);
        Controller.get().getAssetsList().removeAll(assetsToRemove);
    }

    private void removeContent(List<Asset> assetsToRemove) {
        List<String> assetsNames = assetsToRemove.stream()
                .map(a -> a.getName())
                .collect(Collectors.toList());
        List<Content> contentToRemove = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> {
                    String name = c.getItem().getName();
                    return assetsNames.contains(name);
                })
                .collect(Collectors.toList());
        Controller.get().getCurrentLocation().getContent().removeAll(contentToRemove);
    }
}
