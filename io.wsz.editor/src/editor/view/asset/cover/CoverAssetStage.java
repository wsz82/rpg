package editor.view.asset.cover;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Cover;
import javafx.stage.Stage;

public class CoverAssetStage extends AssetStage<Cover> {

    public CoverAssetStage(Stage parent, Cover asset, boolean isContent,
                           EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public CoverAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
    }

    @Override
    protected void addAssetToList(Cover asset) {
        controller.getObservableAssets().getCovers().add(asset);
    }

    @Override
    protected Cover getNewAsset() {
        return new Cover(controller);
    }

}
