package editor.view.asset;

import io.wsz.model.item.Creature;
import io.wsz.model.item.ItemType;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

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
    protected void removeAssetFromList(List<Creature> assetsToRemove) {
        ObservableAssets.get().getCreatures().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.CREATURE;
    }
}
