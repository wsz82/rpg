package editor.view.asset.teleport;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.asset.coords.CoordsEdit;
import editor.view.asset.coords.CoordsPolygonsEditStage;
import editor.view.asset.coords.PolygonsSetter;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Teleport;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.List;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";

    private final Button teleportCollisionButton = new Button("Teleport area");

    private CoordsEdit exitEdit;

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

        container.getChildren().addAll(teleportCollisionButton);

        fillInputs();
        hookUpTeleportEvents();
    }

    private void hookUpTeleportEvents() {
        teleportCollisionButton.setOnAction(e -> openTeleportAreaEdit());
    }

    private void openTeleportAreaEdit() {
        ResolutionImage image = item.getImage();
        if (image == null) {
            return;
        }
        Image background = image.getFxImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> teleportAreaPolygons = item.getIndividualTeleportCollisionPolygons();
        PolygonsSetter teleportAreaSetter = item::setTeleportCollisionPolygons;
        CoordsPolygonsEditStage<Teleport> collisionEdit =
                new CoordsPolygonsEditStage<>(this, teleportAreaPolygons, item, background, teleportAreaSetter);
        collisionEdit.initWindow(false, "Teleport area edit");
        collisionEdit.show();
    }

    @Override
    protected void fillInputs() {
        exitEdit = new CoordsEdit(item.getIndividualExit(), isContent, editorCanvas, editorController);
        exitEdit.initCoords(container);

        super.fillInputs();
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
        Coords exit = item.getIndividualExit();
        item.setExit(exitEdit.defineCoords(exit));
    }

    @Override
    protected void addAssetToList(Teleport asset) {
        editorController.getObservableAssets().getTeleports().add(asset);
    }

    @Override
    protected Teleport getNewAsset() {
        return new Teleport(controller);
    }

}
