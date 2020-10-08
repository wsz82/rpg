package editor.view.asset.cover;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.asset.lists.ObservableItemsList;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Cover;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CoverTableView extends AssetsTableView<Cover> {

    public CoverTableView(Stage parent, ObservableList<Cover> assets,
                   EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
    }

    @Override
    protected void editAsset() {
        Cover c = getSelectionModel().getSelectedItem();
        if (c == null) {
            return;
        }
        CoverAssetStage as = new CoverAssetStage(parent, c, false, editorCanvas, controller);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        CoverAssetStage as = new CoverAssetStage(parent, editorCanvas, controller);
        as.show();
    }

    @Override
    protected List<Cover> createItems(Coords rawPos) {
        List<Cover> selectedAssets = getSelectionModel().getSelectedItems();
        List<Cover> output = new ArrayList<>(1);
        for (Cover p
                : selectedAssets) {
            Cover cover = new Cover(p);
            clonePrototypePos(rawPos, p, cover);
            output.add(cover);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Cover> assetsToRemove) {
        controller.getObservableAssets().getCovers().removeAll(assetsToRemove);
    }

    @Override
    protected List<Cover> getConcreteObservableItems(ObservableItemsList itemsList) {
        return itemsList.getCovers();
    }

}