package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

class LandscapeTableView extends AssetsTableView<Landscape> {

    LandscapeTableView(Stage parent, ObservableList<Landscape> assets) {
        super(parent, assets);
    }

    @Override
    protected void editAsset() {
        Landscape l = getSelectionModel().getSelectedItem();
        if (l == null) {
            return;
        }
        LandscapeAssetStage as = new LandscapeAssetStage(parent, l, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        LandscapeAssetStage as = new LandscapeAssetStage(parent);
        as.show();
    }

    @Override
    protected void removeAssetFromList(List<Landscape> assetsToRemove) {
        ObservableAssets.get().getLandscapes().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.LANDSCAPE;
    }
}