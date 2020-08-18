package editor.view.asset;

import editor.model.EditorController;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.OutDoor;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class OutDoorTableView extends AssetsTableView<OutDoor> {

    OutDoorTableView(Stage parent, ObservableList<OutDoor> assets,
                     EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initOutDoorTable();
    }

    private void initOutDoorTable() {
        TableColumn<OutDoor, String> openDoorPathCol = new TableColumn<>("Open door path");
        openDoorPathCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getOpenImagePath();
            }
        });
        openDoorPathCol.setCellFactory(TextFieldTableCell.forTableColumn());
        openDoorPathCol.setEditable(false);
        getColumns().add(openDoorPathCol);

        TableColumn<OutDoor, String> exitCol = new TableColumn<>("Exit");
        exitCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getExit().toString();
            }
        });
        exitCol.setCellFactory(TextFieldTableCell.forTableColumn());
        exitCol.setEditable(false);
        getColumns().add(exitCol);
    }

    @Override
    protected void editAsset() {
        OutDoor id = getSelectionModel().getSelectedItem();
        if (id == null) {
            return;
        }
        OutDoorAssetStage as = new OutDoorAssetStage(parent, id, false, editorCanvas, editorController);
        as.show();
    }

    @Override
    protected void addAsset() {
        OutDoorAssetStage as = new OutDoorAssetStage(parent, editorCanvas, editorController);
        as.show();
    }

    @Override
    protected List<OutDoor> createItems(Coords rawPos) {
        List<OutDoor> selectedAssets = getSelectionModel().getSelectedItems();
        List<OutDoor> output = new ArrayList<>(1);
        for (OutDoor p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            OutDoor id = new OutDoor(p, true);
            id.setPos(pos);
            id.setOpenImagePath(p.getOpenImagePath());
            id.setOpen(p.isOpen());

            output.add(id);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<OutDoor> assetsToRemove) {
        editorController.getObservableAssets().getOutDoors().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.OUTDOOR;
    }
}