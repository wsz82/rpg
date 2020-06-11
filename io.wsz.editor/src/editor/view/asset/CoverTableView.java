package editor.view.asset;

import io.wsz.model.item.Cover;
import io.wsz.model.item.ItemType;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

public class CoverTableView extends AssetsTableView<Cover> {

    CoverTableView(Stage parent, ObservableList<Cover> assets) {
        super(parent, assets);
    }

    @Override
    protected void editAsset() {
        Cover c = getSelectionModel().getSelectedItem();
        if (c == null) {
            return;
        }
        CoverAssetStage as = new CoverAssetStage(parent, c, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        CoverAssetStage as = new CoverAssetStage(parent);
        as.show();
    }

    @Override
    protected void removeAssetFromList(List<Cover> assetsToRemove) {
        ObservableAssets.get().getCovers().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.COVER;
    }
}