package editor.view.asset;

import editor.view.DoubleField;
import editor.view.IntegerField;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";
    private final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
    private final DoubleField inputX = new DoubleField(0.0, isContent);
    private final DoubleField inputY = new DoubleField(0.0, isContent);
    private final IntegerField inputLayer = new IntegerField(isContent);
    private final Button teleportCollisionButton = new Button("Teleport area");

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

        final HBox location = new HBox(10);
        location.setAlignment(Pos.CENTER_LEFT);
        final Label locationLabel = new Label("To location");
        location.getChildren().addAll(locationLabel, locationChoice);

        final HBox pos = new HBox(10);
        pos.setAlignment(Pos.CENTER_LEFT);
        final Label xLabel = new Label("X:");
        final Label yLabel = new Label("Y:");
        final Label layerLabel = new Label("Layer:");
        inputX.setPrefWidth(50);
        inputY.setPrefWidth(50);
        inputLayer.setText("0");
        inputLayer.setPrefWidth(50);
        pos.getChildren().addAll(xLabel, inputX, yLabel, inputY, layerLabel, inputLayer);

        container.getChildren().addAll(location, pos);

        if (item != null) {
            if (!isContent) {
                container.getChildren().addAll(teleportCollisionButton);
            }
        }

        setUpLocationChoice(locationChoice);
        fillInputs();
        hookUpInDoorEvents();
    }

    private void hookUpInDoorEvents() {
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

        super.fillInputs();

        Coords exit = item.getIndividualExit();
        if (exit == null) {
            locationChoice.setValue(null);
            inputLayer.setText("");
            inputX.setText("");
            inputY.setText("");
        } else {
            locationChoice.setValue(exit.getLocation());
            inputLayer.setText(String.valueOf(exit.level));
            double x = exit.x;
            double y = exit.y;
            inputX.setText(String.valueOf(x));
            inputY.setText(String.valueOf(y));
        }
    }

    @Override
    protected void defineAsset() {
        Location l = locationChoice.getValue();
        String exitLevel = inputLayer.getText();
        String exitX = inputX.getText();
        String exitY = inputY.getText();

        Coords exit = item.getIndividualExit();
        if (exit == null && (l != null || !exitLevel.isEmpty() || !exitX.isEmpty() || !exitY.isEmpty())) {
            exit = new Coords();
        }

        if (l == null) {
            if (exit != null) {
                exit.setLocation(null);
            }
        } else {
            exit.setLocation(l);
        }

        if (exitLevel.isEmpty()) {
            if (exit != null) {
                exit.level = 0;
            }
        } else {
            exit.level = Integer.parseInt(exitLevel);
        }

        boolean posIsEmpty = exitX.isEmpty() || exitY.isEmpty();
        if (posIsEmpty) {
            if (exit != null) {
                exit.x = 0;
                exit.y = 0;
            }
        } else {
            double x = Double.parseDouble(exitX);
            double y = Double.parseDouble(exitY);
            exit.x = x;
            exit.y = y;
        }
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
