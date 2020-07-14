package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
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
    protected List<Landscape> createItems(Coords rawPos) {
        List<Landscape> selectedAssets = getSelectionModel().getSelectedItems();
        List<Landscape> output = new ArrayList<>(1);
        for (Landscape p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Landscape l = new Landscape(p, name, type, path, true);
            l.setPos(pos);
            output.add(l);
        }
        return output;
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