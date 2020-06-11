package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

public class TeleportTableView extends AssetsTableView<Teleport> {

    TeleportTableView(Stage parent, ObservableList<Teleport> assets) {
        super(parent, assets);
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
    protected void removeAssetFromList(List<Teleport> assetsToRemove) {
        ObservableAssets.get().getTeleports().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
