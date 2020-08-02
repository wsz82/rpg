package editor.view.asset;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AssetsTableView<A extends PosItem> extends TableView<A> {
    private static final double MAX_HEIGHT = 8000;
    private static final double MAX_WIDTH = 8000;

    protected final EditorCanvas editorCanvas;
    protected final Stage parent;
    protected final ObservableList<A> assets;

    private Pointer pointer;
    private ContentTableView contentTableView;

    AssetsTableView(Stage parent, ObservableList<A> assets, EditorCanvas editorCanvas) {
        super();
        this.parent = parent;
        this.assets = assets;
        this.editorCanvas = editorCanvas;
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initTable();
        setItems(assets);
        setUpContextMenu();
        setEditable(true);
        hookupEvents();
    }

    private void hookupEvents() {
        setOnDragDetected(e -> {
            e.consume();
            List<A> selectedAssets = getSelectionModel().getSelectedItems();
            if (selectedAssets == null || selectedAssets.isEmpty()) {
                return;
            }
            A firstAsset = selectedAssets.get(0);

            Dragboard db = startDragAndDrop(TransferMode.COPY);

            ClipboardContent content = new ClipboardContent();
            content.putImage(firstAsset.getImage());
            db.setContent(content);
        });

        setOnDragDone(e -> {
            e.consume();
            if (e.getTransferMode() == TransferMode.COPY) {
                EditorController editorController = EditorController.get();
                Coords dragPos = editorController.getDragPos();
                ItemsStage itemsStage;
                if (dragPos.x != -1) {
                    addItemsToStage(dragPos);
                    dragPos.x = -1;
                } else if ((itemsStage = editorController.getItemsStageToAddItems()) != null) {
                    addItemsToContainable(itemsStage);
                    editorController.setItemsStageToAddItems(null);
                }
            }
        });
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
            Coords mark = pointer.getMark();
            Coords cloned = mark.clonePos();
            cloned.setLocation(Controller.get().getCurrentLocation().getLocation());
            addItemsToStage(mark);
        });
        contextMenu.getItems().addAll(addAsset, editAsset, removeAsset, addItemsToStage);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void addItemsToStage(Coords pos) {
        List<A> createdItems = createItems(pos);
        for (A item : createdItems) {
            Image img = item.getImage();
            if (isImageTooBig(item, img)) continue;
            Controller.get().getCurrentLocation().getItems().add(item);
        }
    }

    private void addItemsToContainable(ItemsStage itemsStage) {
        Coords pos = new Coords(0, 0);
        List<A> createdItems = createItems(pos);
        for (A item : createdItems) {
            if (item instanceof Equipment) {
                Image img = item.getImage();
                if (isImageTooBig(item, img)) continue;
                Equipment e = (Equipment) item;
                itemsStage.addEquipment(e);
            }
        }
    }

    private boolean isImageTooBig(A item, Image img) {
        double width = img.getWidth();
        double height = img.getHeight();
        if (width > MAX_WIDTH || height > MAX_HEIGHT) {
            alertTooBigImage(width, height, item.getName());
            return true;
        }
        return false;
    }

    private void alertTooBigImage(double width, double height, String itemName) {
        String message =
                "Image of asset \"" + itemName + "\" is too big: " + "\n"
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
