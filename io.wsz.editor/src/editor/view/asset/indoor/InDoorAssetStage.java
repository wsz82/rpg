package editor.view.asset.indoor;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.InDoor;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.OpenableItem;
import javafx.stage.Stage;

public class InDoorAssetStage extends AssetStage<InDoor> {
    private static final String TITLE = "InDoor asset";

    private OpenableInDoor openable;

    public InDoorAssetStage(Stage parent, InDoor item, boolean isContent,
                            EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, item, isContent, editorCanvas, editorController);
        initWindow();
    }

    public InDoorAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
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
        if (item == null) {
            item = createNewAsset();
        }

        OpenableItem openableItem = item.getOpenableItem();
        openable = new OpenableInDoor(this, item, openableItem, isContent);
        openable.initOpenable(container);

        super.fillInputs();
        openable.fillOpenableInputs();
    }

    @Override
    protected void defineAsset() {
        openable.defineOpenable();
    }

    @Override
    protected void addAssetToList(InDoor asset) {
        editorController.getObservableAssets().getInDoors().add(asset);
    }

    @Override
    protected InDoor createNewAsset() {
        return new InDoor(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.INDOOR;
    }
}