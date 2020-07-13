package editor.view.asset;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Inventory;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Task;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

class CreatureTableView extends AssetsTableView<Creature> {

    CreatureTableView(Stage parent, ObservableList<Creature> assets) {
        super(parent, assets);
        initCreatureTable();
    }

    private void initCreatureTable() {
        TableColumn<Creature, String> controlCol = new TableColumn<>("Control");
        controlCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getControl().toString();
            }
        });
        controlCol.setCellFactory(TextFieldTableCell.forTableColumn());
        controlCol.setEditable(false);
        getColumns().add(controlCol);

        TableColumn<Creature, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getSize().toString();
            }
        });
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sizeCol.setEditable(false);
        getColumns().add(sizeCol);
    }

    @Override
    protected void editAsset() {
        Creature cr = getSelectionModel().getSelectedItem();
        if (cr == null) {
            return;
        }
        CreatureAssetStage as = new CreatureAssetStage(parent, cr, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        CreatureAssetStage as = new CreatureAssetStage(parent);
        as.show();
    }

    @Override
    protected List<Creature> createItems(Coords rawPos, int level) {
        List<Creature> selectedAssets = getSelectionModel().getSelectedItems();
        List<Creature> output = new ArrayList<>(1);
        for (Creature p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Creature cr = new Creature(
                    p, name, type, path,
                    true, level);
            cr.setPos(pos);

            Task pTasks = p.getTask();
            pTasks.clone(cr);

            cr.setInventory(new Inventory(cr));
            cr.getInventory().getItems().addAll(p.getInventory().getItems());

            output.add(cr);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Creature> assetsToRemove) {
        ObservableAssets.get().getCreatures().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.CREATURE;
    }
}
