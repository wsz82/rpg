package editor.view.asset;

import editor.view.DoubleField;
import editor.view.IntegerField;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Teleport;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";
    private final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
    private final DoubleField inputX = new DoubleField(0.0);
    private final DoubleField inputY = new DoubleField(0.0);
    private final IntegerField inputLayer = new IntegerField();

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

        setUpLocationChoice(locationChoice);
        fillInputs();
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
                if (s.isEmpty()) {
                    return null;
                }
                return getLocation(s);
            }
        });
        locationChoice.setItems(Controller.get().getLocationsList());
    }

    private Location getLocation(String s) {
        List<Location> singleLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(s))
                .collect(Collectors.toList());
        if (singleLocation.isEmpty()) {
            return null;
        }
        return singleLocation.get(0);
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (asset == null) {
            return;
        }
        String locationName = asset.getLocationName();
        if (locationName != null) {
            locationChoice.setValue(getLocation(locationName));
        }
        inputLayer.setText(""+asset.getExitLevel());
        Coords exitPos = asset.getExit();
        if (exitPos != null) {
            double x = exitPos.x;
            double y = exitPos.y;
            inputX.setText(""+x);
            inputY.setText(""+y);
        }
    }

    @Override
    protected void defineAsset() {
        if (locationChoice.getValue() != null) {
            String locationName = locationChoice.getValue().getName();
            asset.setLocationName(locationName);
        }
        int layer = Integer.parseInt(inputLayer.getText());
        asset.setExitLevel(layer);
        int x = Integer.parseInt(inputX.getText());
        int y = Integer.parseInt(inputY.getText());
        Coords exitPos = new Coords(x, y);
        asset.setExit(exitPos);
    }

    @Override
    protected void addAssetToList(Teleport asset) {
        ObservableAssets.get().getTeleports().add(asset);
    }

    @Override
    protected Teleport createNewAsset(String name, String relativePath) {
        return new Teleport(
                null, name, getType(), relativePath,
                true, null, null, null, null);
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
