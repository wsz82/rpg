package editor.view.asset;

import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Cover;
import io.wsz.model.item.ItemType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CoverTableView extends AssetsTableView<Cover> {

    CoverTableView(Stage parent, ObservableList<Cover> assets, EditorCanvas editorCanvas) {
        super(parent, assets, editorCanvas);
    }

    @Override
    protected void editAsset() {
        Cover c = getSelectionModel().getSelectedItem();
        if (c == null) {
            return;
        }
        CoverAssetStage as = new CoverAssetStage(parent, c, false, editorCanvas);
        as.show();
    }

    @Override
    protected void addAsset() {
        CoverAssetStage as = new CoverAssetStage(parent, editorCanvas);
        as.show();
    }

    @Override
    protected List<Cover> createItems(Coords rawPos) {
        List<Cover> selectedAssets = getSelectionModel().getSelectedItems();
        List<Cover> output = new ArrayList<>(1);
        for (Cover p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Cover cover = new Cover(
                    p, name, type, path,
                    true);
            cover.setPos(pos);
            output.add(cover);
        }
        return output;
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