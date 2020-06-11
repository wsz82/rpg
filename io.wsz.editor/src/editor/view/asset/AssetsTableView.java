package editor.view.asset;

import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.Asset;
import io.wsz.model.item.AssetConverter;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

abstract class AssetsTableView<A extends Asset> extends TableView<A> {
    protected final Stage parent;
    protected final ObservableList<A> assets;

    AssetsTableView(Stage parent, ObservableList<A> assets) {
        super();
        this.parent = parent;
        this.assets = assets;
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initTable();
        setItems(assets);
        setUpContextMenu();
        setEditable(true);
    }

    protected abstract void editAsset();

    protected abstract void addAsset();

    private void initTable() {
        TableColumn<A, String> nameCol = new TableColumn<>("Name");
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

        TableColumn<A, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(new PropertyValueFactory<>("relativePath"));
        pathCol.setEditable(false);
        pathCol.setCellFactory(TextFieldTableCell.forTableColumn());

        ObservableList<TableColumn<A, ?>> columns = getColumns();
        columns.addAll(nameCol, pathCol);
    }

    private boolean assetNameIsNotUnique(String newValue) {
        return Controller.get().getAssetsList().stream()
                .anyMatch(p -> p.getName().equals(newValue));
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

    private void addItemsToStageAndContents(Coords pos) {
        List<A> selectedAssets = getSelectionModel().getSelectedItems();
        int level = Controller.get().getCurrentLayer().getLevel();
        for (Asset a
                : selectedAssets) {
            if (!pos.is0()) {
                double height = a.getImage().getHeight();
                pos.y = pos.y - (int) height;
            }
            Content c = AssetConverter.convertToContent(a, pos, level);
            Controller.get().getCurrentLocation().getContent().add(c);
        }
    }

    private void removeAssets() {
        List<A> assetsToRemove = getSelectionModel().getSelectedItems();
        removeContent(assetsToRemove);
        removeAssetFromList(assetsToRemove);
    }

    protected abstract void removeAssetFromList(List<A> assetsToRemove);

    private void removeContent(List<A> assetsToRemove) {
        List<String> assetsNames = assetsToRemove.stream()
                .map(Asset::getName)
                .collect(Collectors.toList());
       Controller.get().getLocationsList().forEach(l -> {
           List<Content> contentToRemove = l.getContents().get().stream()
                    .filter(c -> {
                        String name = c.getItem().getName();
                        return assetsNames.contains(name);
                    })
                    .collect(Collectors.toList());
           l.getContents().get().removeAll(contentToRemove);
        });
    }

    protected abstract ItemType getType();
}
