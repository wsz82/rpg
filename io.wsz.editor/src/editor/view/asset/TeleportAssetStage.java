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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeleportAssetStage extends AssetStage<Teleport> {
    private static final String TITLE = "Teleport asset";
    private final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
    private final DoubleField inputX = new DoubleField(0.0, isContent);
    private final DoubleField inputY = new DoubleField(0.0, isContent);
    private final IntegerField inputLayer = new IntegerField(isContent);

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
        if (item == null) {
            return;
        }
        String locationName = item.getIndividualLocationName();
        if (locationName == null) {
            locationChoice.setValue(null);
        } else {
            locationChoice.setValue(getLocation(locationName));
        }
        Integer exitLevel = item.getIndividualExitLevel();
        if (exitLevel == null) {
            inputLayer.setText("");
        } else {
            inputLayer.setText(String.valueOf(exitLevel));
        }
        Coords exitPos = item.getIndividualExit();
        if (exitPos == null) {
            inputX.setText("");
            inputY.setText("");
        } else {
            double x = exitPos.x;
            double y = exitPos.y;
            inputX.setText(String.valueOf(x));
            inputY.setText(String.valueOf(y));
        }
    }

    @Override
    protected void defineAsset() {
        Location l = locationChoice.getValue();
        if (l == null) {
            item.setLocationName(null);
        } else {
            item.setLocationName(l.getName());
        }

        String exitLevel = inputLayer.getText();
        if (exitLevel.isEmpty()) {
            if (isContent) {
                item.setExitLevel(null);
            } else {
                item.setExitLevel(0);
            }
        } else {
            item.setExitLevel(Integer.parseInt(exitLevel));
        }

        String exitX = inputX.getText();
        String exitY = inputY.getText();
        boolean posIsEmpty = exitX.isEmpty() || exitY.isEmpty();
        if (posIsEmpty) {
            if (isContent) {
                item.setExit(null);
            } else {
                item.setExit(new Coords(0, 0));
            }
        } else {
            double x = Double.parseDouble(exitX);
            double y = Double.parseDouble(exitY);
            Coords exitPos = new Coords(x, y);
            item.setExit(exitPos);
        }
    }

    @Override
    protected void addAssetToList(Teleport asset) {
        ObservableAssets.get().getTeleports().add(asset);
    }

    @Override
    protected Teleport createNewAsset(String name, String relativePath) {
        return new Teleport(
                null, name, getType(), relativePath,
                true, null,
                new ArrayList<>(0), new ArrayList<>(0));
    }

    @Override
    protected ItemType getType() {
        return ItemType.TELEPORT;
    }
}
