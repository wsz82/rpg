package editor.view.asset;

import editor.model.EditorController;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

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
        Image background = item.getImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> teleportAreaPolygons = item.getTeleportCollisionPolygons();
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(this, teleportAreaPolygons, item, background);
        collisionEdit.initWindow(isContent, "Teleport area edit");
        collisionEdit.show();
    }

    private void setUpLocationChoice(ChoiceBox<Location> locationChoice) {
        locationChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(Location l) {
                if (l == null) {
                    return "";
                }
                return l.getName();
            }

            @Override
            public Location fromString(String s) {
                if (s == null) {
                    return null;
                }
                if (s.isEmpty()) {
                    return null;
                }
                return getLocation(s);
            }
        });
        ObservableList<Location> locations = FXCollections.observableArrayList(editorController.getObservableLocations());
        locationChoice.setItems(locations);
        if (isContent) {
            locations.add(null);
        }
    }

    private Location getLocation(String s) {
        Optional<Location> optLocation = controller.getLocations().stream()
                .filter(l -> l.getName().equals(s))
                .findFirst();
        return optLocation.orElse(null);
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
        Coords exit = item.getIndividualExit();
        item.setExit(coordsEdit.defineCoords(exit));
    }

    @Override
    protected void addAssetToList(Teleport asset) {
        editorController.getObservableAssets().getTeleports().add(asset);
    }

    @Override
    protected Teleport createNewAsset() {
        return new Teleport(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
