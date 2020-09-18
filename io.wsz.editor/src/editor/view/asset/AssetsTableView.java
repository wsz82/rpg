package editor.view.asset;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
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

import java.util.List;
import java.util.stream.Collectors;

public abstract class AssetsTableView<A extends PosItem<?,?>> extends TableView<A> {
    protected final EditorCanvas editorCanvas;
    protected final EditorController editorController;
    protected final Controller controller;
    protected final Stage parent;

    private Pointer pointer;
    private ContentTableView contentTableView;

    public AssetsTableView(Stage parent, ObservableList<A> assets, EditorCanvas editorCanvas, EditorController editorController) {
        super();
        this.parent = parent;
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
        controller = editorController.getController();
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        initAssetsTable();
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

    protected void refreshTableOnStageHidden(Stage toHide) {
        toHide.setOnHiding(e -> {
            this.refresh();
        });
    }

    private void initAssetsTable() {
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
    }

    private boolean assetIdIsNotUnique(String newValue) {
        return editorController.getObservableAssets().getMergedAssets().stream()
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
            cloned.setLocation(controller.getCurrentLocation().getLocation());
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
            controller.getCurrentLocation().getItems().add(item);
        }
    }

    private void addItemsToContainable(ItemsStage itemsStage) {
        Coords pos = new Coords(0, 0);
        List<A> createdItems = createItems(pos);
        for (A item : createdItems) {
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
        List<String> assetsNames = assetsToRemove.stream()
                .map(Asset::getAssetId)
                .collect(Collectors.toList());
       editorController.getObservableLocations().forEach(location -> {
           List<PosItem> items = location.getItems();
           List<PosItem> contentToRemove = items.stream()
                    .filter(p -> {
                        String name = p.getAssetId();
                        if (p instanceof Containable) {
                            Containable c = (Containable) p;
                            removeItemsFromContainable(assetsNames, c);
                        }
                        return assetsNames.contains(name);
                    })
                    .collect(Collectors.toList());
           items.removeAll(contentToRemove);
        });
    }

    private void removeItemsFromContainable(List<String> assetsNames, Containable c) {
        List<Equipment> equipment = c.getItems();
        if (equipment == null || equipment.isEmpty()) return;
        List<Equipment> equipmentToRemove = equipment.stream()
                .filter(e -> {
                    if (e instanceof Containable) {
                        removeItemsFromContainable(assetsNames, (Containable) e);
                    }
                    return assetsNames.contains(e.getAssetId());
                })
                .collect(Collectors.toList());
        equipment.removeAll(equipmentToRemove);
    }

    protected abstract ItemType getType();

    public void setPointer(Pointer p) {
        pointer = p;
    }

    public void setContentTableView(ContentTableView contentTableView) {
        this.contentTableView = contentTableView;
    }
}
