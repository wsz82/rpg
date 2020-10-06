package editor.view.asset;

import editor.model.EditorController;
import editor.view.asset.items.ItemsStage;
import editor.view.content.ContentTableView;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AssetsTableView<A extends PosItem<?,?>> extends TableView<A> {
    protected final EditorCanvas editorCanvas;
    protected final EditorController controller;
    protected final Stage parent;

    private Pointer pointer;
    private ContentTableView contentTableView;

    public AssetsTableView(Stage parent, ObservableList<A> assets, EditorCanvas editorCanvas, EditorController controller) {
        super();
        this.parent = parent;
        this.editorCanvas = editorCanvas;
        this.controller = controller;
        setItems(assets);
        initAssetsTable();
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
            ResolutionImage image = firstAsset.getImage();
            if (image == null) return;
            Image fxImage = image.getFxImage();
            if (fxImage == null) return;
            content.putImage(fxImage);
            db.setContent(content);
        });

        setOnDragDone(e -> {
            e.consume();
            if (e.getTransferMode() == TransferMode.COPY) {
                Coords dragPos = controller.getDragPos();
                ItemsStage itemsStage;
                if (dragPos.x != -1) {
                    addItemsToStage(dragPos);
                    dragPos.x = -1;
                } else if ((itemsStage = controller.getItemsStageToAddItems()) != null) {
                    addItemsToContainable(itemsStage);
                    controller.setItemsStageToAddItems(null);
                }
            }
        });
    }

    protected abstract void editAsset();

    protected abstract void addAsset();

    protected void refreshTableOnStageHidden(Stage toHide) {
        toHide.setOnHiding(e -> {
            this.refresh();
        });
    }

    private void initAssetsTable() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<A, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getAssetId();
            }
        });
        idCol.setEditable(true);
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(t -> {
            Asset asset = getSelectionModel().getSelectedItem();
            String newValue = t.getNewValue();
            if (assetIdIsNotUnique(newValue)) {
                asset.setAssetId(t.getOldValue());
            } else {
                asset.setAssetId(newValue);
                contentTableView.refresh();
            }
            refresh();
        });

        TableColumn<A, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getIndividualName();
            }
        });
        nameCol.setEditable(false);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<A, String> pathCol = new TableColumn<>("Path");
        pathCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getPath();
            }
        });
        pathCol.setEditable(false);
        pathCol.setCellFactory(TextFieldTableCell.forTableColumn());

        ObservableList<TableColumn<A, ?>> columns = getColumns();
        columns.addAll(idCol, nameCol, pathCol);

        setUpContextMenu();
    }

    private boolean assetIdIsNotUnique(String newValue) {
        return controller.getObservableAssets().getMergedAssets().stream()
                .anyMatch(p -> p.getAssetId().equals(newValue));
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
            cloned.setLocation(controller.getCurrentObservableLocation().getLocation());
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
            Image img = item.getImage().getFxImage();
            if (isImageTooBig(item, img)) continue;
            controller.getCurrentObservableLocation().getItems().add(item);
        }
    }

    private void addItemsToContainable(ItemsStage itemsStage) {
        Coords pos = new Coords(0, 0);
        List<A> createdItems = createItems(pos);
        for (A item : createdItems) { //TODO generic
            if (item instanceof Equipment) {
                Image img = item.getImage().getFxImage();
                if (isImageTooBig(item, img)) continue;
                Equipment e = (Equipment) item;
                itemsStage.addEquipment(e);
            }
        }
    }

    private boolean isImageTooBig(A item, Image img) {
        double width = img.getWidth();
        double height = img.getHeight();
        if (width > Sizes.MAX_IMAGE_WIDTH || height > Sizes.MAX_IMAGE_HEIGHT) {
            alertTooBigImage(width, height, item.getAssetId());
            return true;
        }
        return false;
    }

    private void alertTooBigImage(double width, double height, String itemName) {
        String message =
                "Image of asset \"" + itemName + "\" is too big: " + "\n"
                + "Width: " + width + "\n"
                + "Height: " + height + "\n"
                + "when maximum is: " + Sizes.MAX_IMAGE_WIDTH;
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

    protected void clonePrototypePos(Coords rawPos, A p, A w) {
        Coords pos = rawPos.clonePos();
        if (!pos.is0()) {
            double height = p.getImage().getHeight() / Sizes.getMeter();
            pos.y = pos.y - height;
        }
        w.setPos(pos);
    }

    private void removeContent(List<A> assetsToRemove) {
        List<String> assetsIds = assetsToRemove.stream()
                .map(Asset::getAssetId)
                .collect(Collectors.toList());
        ObservableList<PosItem<?, ?>> observableItems = controller.getCurrentObservableLocation().getItems();
        controller.getObservableLocations().forEach(location -> {
           ItemsList itemsList = location.getItemsList();
           List<PosItem<?,?>> contentToRemove = new ArrayList<>();
           itemsList.forEach(i -> {
               for (String assetId : assetsIds) {
                   i.addItemToListByAssetId(contentToRemove, assetId);
               }
           });
           itemsList.removeAll(contentToRemove);
           observableItems.removeAll(contentToRemove);
        });
    }

    public void setPointer(Pointer p) {
        pointer = p;
    }

    public void setContentTableView(ContentTableView contentTableView) {
        this.contentTableView = contentTableView;
    }
}
