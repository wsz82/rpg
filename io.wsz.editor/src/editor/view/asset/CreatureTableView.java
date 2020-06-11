package editor.view.asset;

import io.wsz.model.item.Creature;
import io.wsz.model.item.ItemType;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

class CreatureTableView extends AssetsTableView<Creature> {

    CreatureTableView(Stage parent, ObservableList<Creature> assets) {
        super(parent, assets);
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
