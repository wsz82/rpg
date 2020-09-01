package editor.view.asset.teleport;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.asset.coords.CoordsEdit;
import editor.view.asset.coords.CoordsPolygonsEditStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import io.wsz.model.stage.Coords;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";

    private final Button teleportCollisionButton = new Button("Teleport area");

    private CoordsEdit coordsEdit;

    public TeleportAssetStage(Stage parent, Teleport asset, boolean isContent,
                              EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public TeleportAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        if (item != null) {
            if (!isContent) {
                container.getChildren().addAll(teleportCollisionButton);
            }
        }

        fillInputs();
        hookUpTeleportEvents();
    }

    private void hookUpTeleportEvents() {
        teleportCollisionButton.setOnAction(e -> openTeleportAreaEdit());
    }

    private void openTeleportAreaEdit() {
        Image background = item.getImage().getFxImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> teleportAreaPolygons = item.getTeleportCollisionPolygons();
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(this, teleportAreaPolygons, item, background);
        collisionEdit.initWindow(isContent, "Teleport area edit");
        collisionEdit.show();
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }
        coordsEdit = new CoordsEdit(item.getIndividualExit(), isContent, editorCanvas, editorController);
        coordsEdit.initCoords(container);

        super.fillInputs();
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
        Coords exit = item.getIndividualExit();
        item.setExit(coordsEdit.defineCoords(exit));
    }

    @Override
    protected void addAssetToList(Teleport asset) {
        editorController.getObservableAssets().getTeleports().add(asset);
    }

    @Override
    protected Teleport createNewAsset() {
        return new Teleport(getType(), controller);
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
