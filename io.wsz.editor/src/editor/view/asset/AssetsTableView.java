package editor.view.asset;

import editor.view.content.ContentTableView;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AssetsTableView<A extends PosItem> extends TableView<A> {
    private static final double MAX_HEIGHT = 8000;
    private static final double MAX_WIDTH = 8000;
    protected final Stage parent;
    protected final ObservableList<A> assets;
    private Pointer pointer;
    private ContentTableView contentTableView;

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
                contentTableView.refresh();
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
        return ObservableAssets.get().merge().stream()
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
            addItemsToStage();
        });
        contextMenu.getItems().addAll(addAsset, editAsset, removeAsset, addItemsToStage);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void addItemsToStage() {
        Coords mark = pointer.getMark();
        Coords clone = mark.clone();
        List<A> createdItems = createItems(clone);
        for (A item : createdItems) {
            Image img = item.getImage();
            double width = img.getWidth();
            double height = img.getHeight();
            if (width > MAX_WIDTH || height > MAX_HEIGHT) {
                alertTooBigImage(width, height, item.getName());
                continue;
            }
            Controller.get().getCurrentLocation().getItems().add(item);
        }
    }

    private void alertTooBigImage(double width, double height, String itemName) {
        String message = "Image of asset \"" + itemName + "\" is too big: " + "\n"
                + "Width: " + width + "\n"
                + "Height: " + height + "\n"
                + "when maximum is: " + MAX_WIDTH;
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, message, ButtonType.CLOSE);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CLOSE)
                .ifPresent(r -> alert.close());
    }

    protected abstract List<A> createItems(Coords pos);

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
           List<PosItem> contentToRemove = l.getItems().get().stream()
                    .filter(p -> {
                        String name = p.getName();
                        return assetsNames.contains(name);
                    })
                    .collect(Collectors.toList());
           l.getItems().get().removeAll(contentToRemove);
        });
    }

    protected abstract ItemType getType();

    public void setPointer(Pointer p) {
        pointer = p;
    }

    public void setContentTableView(ContentTableView contentTableView) {
        this.contentTableView = contentTableView;
    }
}
