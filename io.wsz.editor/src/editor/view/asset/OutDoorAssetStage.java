package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.OutDoor;
import javafx.stage.Stage;

import java.util.ArrayList;

public class OutDoorAssetStage extends AssetStage<OutDoor> {
    private static final String TITLE = "OutDoor asset";

    private final OpenableOutDoor openable = new OpenableOutDoor(this, item, isContent);

    public OutDoorAssetStage(Stage parent, OutDoor item, boolean isContent) {
        super(parent, item, isContent);
        initWindow();
    }

    public OutDoorAssetStage(Stage parent) {
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
    protected void addAssetToList(OutDoor asset) {
        ObservableAssets.get().getOutDoors().add(asset);
    }

    @Override
    protected OutDoor createNewAsset(String name, String relativePath) {
        OutDoor i = new OutDoor(null, name, getType(), relativePath, true);
        i.setOpenDoorCoverLine(new ArrayList<>(0));
        i.setOpenDoorCollisionPolygons(new ArrayList<>(0));
        return i;
    }

    @Override
    protected ItemType getType() {
        return ItemType.OUTDOOR;
    }
}