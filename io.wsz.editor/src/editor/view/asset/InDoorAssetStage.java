package editor.view.asset;

import editor.view.stage.EditorCanvas;
import io.wsz.model.item.InDoor;
import io.wsz.model.item.ItemType;
import javafx.stage.Stage;

public class InDoorAssetStage extends AssetStage<InDoor> {
    private static final String TITLE = "InDoor asset";

    private final OpenableInDoor openable = new OpenableInDoor(this, item, isContent);

    public InDoorAssetStage(Stage parent, InDoor item, boolean isContent, EditorCanvas editorCanvas) {
        super(parent, item, isContent, editorCanvas);
        initWindow();
    }

    public InDoorAssetStage(Stage parent, EditorCanvas editorCanvas) {
        super(parent, editorCanvas);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        openable.initOpenable(container);
        fillInputs();
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }

        super.fillInputs();

        openable.fillOpenableInputs();
    }

    @Override
    protected void defineAsset() {
        openable.defineOpenable();
    }

    @Override
    protected void addAssetToList(InDoor asset) {
        ObservableAssets.get().getInDoors().add(asset);
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