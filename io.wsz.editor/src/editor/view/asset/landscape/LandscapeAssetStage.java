package editor.view.asset.landscape;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Landscape;
import javafx.stage.Stage;

public class LandscapeAssetStage extends AssetStage<Landscape> {

    public LandscapeAssetStage(Stage parent, Landscape asset, boolean isContent,
                               EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public LandscapeAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
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
    protected void addAssetToList(Landscape asset) {
        controller.getObservableAssets().getLandscapes().add(asset);
    }

    @Override
    protected Landscape getNewAsset() {
        return new Landscape(controller);
    }

}