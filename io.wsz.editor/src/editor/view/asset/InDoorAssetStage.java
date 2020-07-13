package editor.view.asset;

import io.wsz.model.item.InDoor;
import io.wsz.model.item.ItemType;
import javafx.stage.Stage;

import java.util.ArrayList;

public class InDoorAssetStage extends AssetStage<InDoor> {
    private static final String TITLE = "InDoor asset";

    private final OpenableInDoor openable = new OpenableInDoor(this, item, isContent);

    public InDoorAssetStage(Stage parent, InDoor item, boolean isContent) {
        super(parent, item, isContent);
        initWindow();
    }

    public InDoorAssetStage(Stage parent) {
        super(parent);
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
    protected InDoor createNewAsset(String name, String relativePath) {
        InDoor i = new InDoor(
                null, name, getType(), relativePath,
                true, null);
        i.setOpenDoorCoverLine(new ArrayList<>(0));
        i.setOpenDoorCollisionPolygons(new ArrayList<>(0));
        return i;
    }

    @Override
    protected ItemType getType() {
        return ItemType.INDOOR;
    }
}