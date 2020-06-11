package editor.view.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
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
    protected void addToStage(Coords pos) {
        List<Landscape> selectedAssets = getSelectionModel().getSelectedItems();
        int level = Controller.get().getCurrentLayer().getLevel();
        for (Landscape p
                : selectedAssets) {
            if (!pos.is0()) {
                double height = p.getImage().getHeight();
                pos.y = pos.y - (int) height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Coords clonePos = new Coords(pos.x, pos.y);
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
            Controller.get().getCurrentLocation().getItems().add(l);
        }
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