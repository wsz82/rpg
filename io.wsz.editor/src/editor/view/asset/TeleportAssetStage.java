package editor.view.asset;

import io.wsz.model.Controller;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";

    private final Button teleportCollisionButton = new Button("Teleport area");

    private CoordsEdit coordsEdit;

    public TeleportAssetStage(Stage parent, Teleport asset, boolean isContent) {
        super(parent, asset, isContent);
        initWindow();
    }

    public TeleportAssetStage(Stage parent) {
        super(parent);
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
        ObservableList<Location> locations = FXCollections.observableArrayList(Controller.get().getLocationsList());
        locationChoice.setItems(locations);
        if (isContent) {
            locations.add(null);
        }
    }

    private Location getLocation(String s) {
        Optional<Location> optLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(s))
                .findFirst();
        return optLocation.orElse(null);
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset("", "");
        }
        coordsEdit = new CoordsEdit(item.getIndividualExit(), isContent);
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
        ObservableAssets.get().getTeleports().add(asset);
    }

    @Override
    protected Teleport createNewAsset(String name, String relativePath) {
        Teleport t = new Teleport(
                null, name, getType(), relativePath,
                true);
        t.setCoverLine(new ArrayList<>(0));
        t.setCollisionPolygons(new ArrayList<>(0));
        return t;
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
