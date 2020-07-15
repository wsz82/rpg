package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.OutDoor;
import io.wsz.model.stage.Coords;
import javafx.stage.Stage;

public class OutDoorAssetStage extends AssetStage<OutDoor> {
    private static final String TITLE = "OutDoor asset";

    private OpenableOutDoor openable;
    private CoordsEdit coordsEdit;

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

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }
        openable = new OpenableOutDoor(this, item, isContent);
        openable.initOpenable(container);
        coordsEdit = new CoordsEdit(item.getIndividualExit(), isContent);
        coordsEdit.initCoords(container);

        super.fillInputs();
        openable.fillOpenableInputs();
    }

    @Override
    protected void defineAsset() {
        openable.defineOpenable();
        Coords exit = item.getIndividualExit();
        item.setExit(coordsEdit.defineCoords(exit));
    }

    @Override
    protected void addAssetToList(OutDoor asset) {
        ObservableAssets.get().getOutDoors().add(asset);
    }

    @Override
    protected OutDoor createNewAsset() {
        return new OutDoor(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.OUTDOOR;
    }
}