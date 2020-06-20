package editor.view.asset;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Inventory;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.Constants.METER;

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
            Coords pos = rawPos.clone();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / METER;
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Coords clone = pos.clone();
            List<Coords> coverLine = new ArrayList<>();
            if (p.getCoverLine() != null) {
                coverLine.addAll(p.getCoverLine());
            }
            List<List<Coords>> collisionPolygons = new ArrayList<>();
            if (p.getCollisionPolygons() != null) {
                collisionPolygons.addAll(p.getCollisionPolygons());
            }

            Creature cr = new Creature(
                    p, name, type, path,
                    true, clone, level,
                    coverLine, collisionPolygons);

            cloneTasks(p, cr);

            cr.setInventory(new Inventory(cr));
            cr.getInventory().getItems().addAll(p.getInventory().getItems());

            output.add(cr);
        }
        return output;
    }

    private void cloneTasks(Creature p, Creature cr) {
        ArrayDeque<Creature.Task> pTasks = p.getTasks();
        ArrayDeque<Creature.Task> tasks = new ArrayDeque<>(pTasks.size());
        for (Creature.Task task : pTasks) {
            tasks.add(task.clone());
        }
        cr.setTasks(tasks);
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
