package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.Constants.METER;

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
    protected List<Landscape> createItems(Coords rawPos, int level) {
        List<Landscape> selectedAssets = getSelectionModel().getSelectedItems();
        List<Landscape> output = new ArrayList<>(1);
        for (Landscape p
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

            Landscape l = new Landscape(
                    p, name, type, path,
                    true, clonePos, level,
                    coverLine, collisionPolygons);
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