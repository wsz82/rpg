package editor.view.asset.landscape;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LandscapeTableView extends AssetsTableView<Landscape> {

    public LandscapeTableView(Stage parent, ObservableList<Landscape> assets,
                       EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
    }

    @Override
    protected void editAsset() {
        Landscape l = getSelectionModel().getSelectedItem();
        if (l == null) {
            return;
        }
        LandscapeAssetStage as = new LandscapeAssetStage(parent, l, false, editorCanvas, editorController);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        LandscapeAssetStage as = new LandscapeAssetStage(parent, editorCanvas, editorController);
        as.show();
    }

    @Override
    protected List<Landscape> createItems(Coords rawPos) {
        List<Landscape> selectedAssets = getSelectionModel().getSelectedItems();
        List<Landscape> output = new ArrayList<>(1);
        for (Landscape p : selectedAssets) {
            Landscape l = new Landscape(p);
            clonePrototypePos(rawPos, p, l);
            output.add(l);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Landscape> assetsToRemove) {
        editorController.getObservableAssets().getLandscapes().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.LANDSCAPE;
    }
}