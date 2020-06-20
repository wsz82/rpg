package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.Constants.METER;

public class TeleportTableView extends AssetsTableView<Teleport> {

    TeleportTableView(Stage parent, ObservableList<Teleport> assets) {
        super(parent, assets);
        initTeleportTable();
    }

    private void initTeleportTable() {
        TableColumn<Teleport, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getLocationName();
            }
        });
        toCol.setCellFactory(TextFieldTableCell.forTableColumn());
        toCol.setEditable(false);
        getColumns().add(toCol);

        TableColumn<Teleport, String> posCol = new TableColumn<>("To pos");
        posCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getExit().toString();
            }
        });
        posCol.setCellFactory(TextFieldTableCell.forTableColumn());
        posCol.setEditable(false);
        getColumns().add(posCol);

        TableColumn<Teleport, String> levCol = new TableColumn<>("To level");
        levCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getExitLevel().toString();
            }
        });
        levCol.setCellFactory(TextFieldTableCell.forTableColumn());
        levCol.setEditable(false);
        getColumns().add(levCol);
    }

    @Override
    protected void editAsset() {
        Teleport t = getSelectionModel().getSelectedItem();
        if (t == null) {
            return;
        }
        TeleportAssetStage as = new TeleportAssetStage(parent, t, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        TeleportAssetStage as = new TeleportAssetStage(parent);
        as.show();
    }

    @Override
    protected List<Teleport> createItems(Coords rawPos, int level) {
        List<Teleport> selectedAssets = getSelectionModel().getSelectedItems();
        List<Teleport> output = new ArrayList<>(1);
        for (Teleport p
                : selectedAssets) {
            Coords pos = rawPos.clone();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / METER;
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Coords clonePos = pos.clone();
            List<Coords> coverLine = new ArrayList<>();
            if (p.getCoverLine() != null) {
                coverLine.addAll(p.getCoverLine());
            }
            List<List<Coords>> collisionPolygons = new ArrayList<>();
            if (p.getCollisionPolygons() != null) {
                collisionPolygons.addAll(p.getCollisionPolygons());
            }

            Teleport t = new Teleport(
                    p, name, type, path,
                    true, clonePos, level,
                    coverLine, collisionPolygons);
            output.add(t);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Teleport> assetsToRemove) {
        ObservableAssets.get().getTeleports().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
