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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
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
        pathCol.setCellValueFactory(new PropertyValueFactory<>("path"));
        pathCol.setEditable(true);
        pathCol.setCellFactory(TextFieldTableCell.forTableColumn());
        pathCol.setOnEditCommit(t -> {
            Asset asset = t.getTableView().getItems().get(t.getTablePosition().getRow());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image for asset");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(parent);
            if (selectedFile != null && selectedFile.isFile()) {
                try {
                    String path = selectedFile.toURI().toURL().toString();
                    asset.setPath(path);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            refresh();
        });

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
            Coords mark = Pointer.getMark();
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
            default -> new AssetStageImpl(parent, assetToEdit);
        };
        assetStage.show();
    }

    void addItemsToStageAndContents(Coords pos) {
        pos.setZ(getUniqueZ(pos.getZ()));
        List<Asset> selectedAssets = getSelectionModel().getSelectedItems();
        List<Content> contents = AssetConverter.convertToContent(selectedAssets, pos);
        Controller.get().getCurrentLocation().getContent().addAll(contents);
    }

    private int getUniqueZ(int z) {
        int level = Controller.get().getCurrentLayer().getLevel();
        List<Integer> zPositions = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getLevel() == level)
                .map(c -> c.getItem().getPos().getZ())
                .collect(Collectors.toList());
        return iterateForUniqueZ(z, zPositions);
    }

    private int iterateForUniqueZ(int z, List<Integer> zPositions) {
        if (!zPositions.contains(z)) {
            return z;
        } else {
            z += 1;
            return iterateForUniqueZ(z, zPositions);
        }
    }

    void addAsset() {
        AssetStage assetStage = switch (itemType) {
            case CREATURE -> new CreatureAssetStage(parent, itemType);
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
