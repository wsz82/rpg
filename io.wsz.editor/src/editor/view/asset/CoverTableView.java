package editor.view.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.Cover;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
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
    protected void addToStage(Coords pos) {
        List<Cover> selectedAssets = getSelectionModel().getSelectedItems();
        int level = Controller.get().getCurrentLayer().getLevel();
        for (Cover c
                : selectedAssets) {
            if (!pos.is0()) {
                double height = c.getImage().getHeight();
                pos.y = pos.y - (int) height;
            }

            String name = c.getName();
            ItemType type = c.getType();
            String path = c.getRelativePath();

            Coords clonePos = new Coords(pos.x, pos.y);
            List<Coords> coverLine = new ArrayList<>();
            if (c.getCoverLine() != null) {
                coverLine.addAll(c.getCoverLine());
            }
            List<List<Coords>> collisionPolygons = new ArrayList<>();
            if (c.getCollisionPolygons() != null) {
                collisionPolygons.addAll(c.getCollisionPolygons());
            }

            Cover cover = new Cover(
                    c, name, type, path,
                    true, clonePos, level,
                    coverLine, collisionPolygons);
            Controller.get().getCurrentLocation().getItems().add(cover);
        }
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