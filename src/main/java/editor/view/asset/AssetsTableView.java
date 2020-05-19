package editor.view.asset;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.asset.Asset;
import model.asset.AssetToContentConverter;
import model.asset.AssetsList;
import model.content.Content;
import model.item.ItemType;
import model.location.CurrentLocation;
import model.stage.Coordinates;
import editor.view.stage.Pointer;

import java.io.File;
import java.net.MalformedURLException;
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
            nameCol.setVisible(false);
            nameCol.setVisible(true);
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
            pathCol.setVisible(false);
            pathCol.setVisible(true);
        });

        ObservableList<TableColumn<Asset, ?>> columns = getColumns();
        columns.addAll(nameCol, pathCol);
    }

    private boolean assetNameIsNotUnique(String newValue) {
        return AssetsList.get().stream()
                .anyMatch(p -> p.getName().equals(newValue));
    }

    private void setFilteredItems() {
        ObservableList<Asset> assets = AssetsList.get();
        FilteredList<Asset> filteredList = new FilteredList<>(assets);
        filteredList.setPredicate(asset -> asset.getType() == itemType);
        setItems(filteredList);
    }

    private void setUpContextMenu() {
        addAsset.setOnAction(event -> addAsset());
        removeAsset.setOnAction(event -> removeAssets());
        addItemsToStage.setOnAction(event -> {
            Coordinates mark = Pointer.getMark();
            addItemsToStageAndContents(mark);
        });
        contextMenu.getItems().addAll(addAsset, removeAsset, addItemsToStage);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    void addItemsToStageAndContents(Coordinates pos) {
        List<Asset> selectedAssets = getSelectionModel().getSelectedItems();
        List<Content> contents = AssetToContentConverter.convert(selectedAssets, pos);
        CurrentLocation.get().getContent().addAll(contents);
    }

    void addAsset() {
        NewAssetStage newAssetStage = new NewAssetStage(parent, itemType);
        newAssetStage.show();
    }

    void removeAssets() {
        ObservableList<Asset> assetsToRemove = getSelectionModel().getSelectedItems();
        AssetsList.get().removeAll(assetsToRemove);
    }
}
