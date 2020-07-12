package editor.view.asset;

import io.wsz.model.item.InDoor;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class InDoorTableView extends AssetsTableView<InDoor> {

    InDoorTableView(Stage parent, ObservableList<InDoor> assets) {
        super(parent, assets);
        initInDoorTable();
    }

    private void initInDoorTable() {
        TableColumn<InDoor, String> openDoorPathCol = new TableColumn<>("Open door path");
        openDoorPathCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getOpenImagePath();
            }
        });
        openDoorPathCol.setCellFactory(TextFieldTableCell.forTableColumn());
        openDoorPathCol.setEditable(false);
        getColumns().add(openDoorPathCol);
    }

    @Override
    protected void editAsset() {
        InDoor id = getSelectionModel().getSelectedItem();
        if (id == null) {
            return;
        }
        InDoorAssetStage as = new InDoorAssetStage(parent, id, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        InDoorAssetStage as = new InDoorAssetStage(parent);
        as.show();
    }

    @Override
    protected List<InDoor> createItems(Coords rawPos, int level) {
        List<InDoor> selectedAssets = getSelectionModel().getSelectedItems();
        List<InDoor> output = new ArrayList<>(1);
        for (InDoor p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            InDoor id = new InDoor(
                    p, name, type, path,
                    true, level,
                    Coords.cloneCoordsList(p.getCoverLine()), Coords.cloneCoordsPolygons(p.getCollisionPolygons()));
            id.setPos(pos);
            id.setOpenImagePath(p.getOpenImagePath());
            id.setOpen(p.isOpen());
            id.setOpenDoorCoverLine(Coords.cloneCoordsList(p.getOpenDoorCoverLine()));
            id.setOpenDoorCollisionPolygons(Coords.cloneCoordsPolygons(p.getOpenDoorCollisionPolygons()));

            output.add(id);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<InDoor> assetsToRemove) {
        ObservableAssets.get().getInDoors().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.INDOOR;
    }
}