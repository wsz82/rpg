package editor.view.asset.equipment.countable.misc;

import editor.model.EditorController;
import editor.view.asset.equipment.countable.EquipmentMayCountableAssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Misc;
import javafx.stage.Stage;

public class MiscAssetStage extends EquipmentMayCountableAssetStage<Misc> {
    private static final String TITLE = "Miscellaneous asset";

    public MiscAssetStage(Stage parent, Misc asset, boolean isContent,
                          EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public MiscAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);
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
    protected void addAssetToList(Misc asset) {
        controller.getObservableAssets().getMiscs().add(asset);
    }

    @Override
    protected Misc getNewAsset() {
        return new Misc(controller);
    }
}
