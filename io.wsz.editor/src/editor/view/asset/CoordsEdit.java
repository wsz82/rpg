package editor.view.asset;

import editor.view.DoubleField;
import editor.view.IntegerField;
import io.wsz.model.Controller;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Optional;

public class CoordsEdit {
    private final Coords pos;
    private final boolean isContent;
    private final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
    private final DoubleField inputX;
    private final DoubleField inputY;
    private final IntegerField inputLayer;

    public CoordsEdit(Coords pos, boolean isContent) {
        this.pos = pos;
        this.isContent = isContent;
        inputX = new DoubleField(0.0, isContent);
        inputY = new DoubleField(0.0, isContent);
        inputLayer = new IntegerField(isContent);
    }

    public void initCoords(VBox container) {
        final HBox location = new HBox(10);
        location.setAlignment(Pos.CENTER_LEFT);
        final Label locationLabel = new Label("To location");
        location.getChildren().addAll(locationLabel, locationChoice);

        final HBox posBox = new HBox(10);
        posBox.setAlignment(Pos.CENTER_LEFT);
        final Label xLabel = new Label("X:");
        final Label yLabel = new Label("Y:");
        final Label layerLabel = new Label("Layer:");
        inputX.setPrefWidth(50);
        inputY.setPrefWidth(50);
        inputLayer.setText("0");
        inputLayer.setPrefWidth(50);
        posBox.getChildren().addAll(xLabel, inputX, yLabel, inputY, layerLabel, inputLayer);

        container.getChildren().addAll(location, posBox);

        setUpLocationChoice(locationChoice);

        fillCoordsInputs();
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

    public void fillCoordsInputs() {
        if (pos == null) {
            locationChoice.setValue(null);
            inputLayer.setText("");
            inputX.setText("");
            inputY.setText("");
        } else {
            locationChoice.setValue(pos.getLocation());
            inputLayer.setText(String.valueOf(pos.level));
            double x = pos.x;
            double y = pos.y;
            inputX.setText(String.valueOf(x));
            inputY.setText(String.valueOf(y));
        }
    }

    public Coords defineCoords(Coords pos) {
        Location l = locationChoice.getValue();
        String exitLevel = inputLayer.getText();
        String exitX = inputX.getText();
        String exitY = inputY.getText();

        if (pos == null && (l != null || !exitLevel.isEmpty() || !exitX.isEmpty() || !exitY.isEmpty())) {
            pos = new Coords();
        }

        if (pos == null) {
            return null;
        }

        pos.setLocation(l);

        if (exitLevel.isEmpty()) {
            pos.level = 0;
        } else {
            pos.level = Integer.parseInt(exitLevel);
        }

        boolean posIsEmpty = exitX.isEmpty() || exitY.isEmpty();
        if (posIsEmpty) {
            pos.x = 0;
            pos.y = 0;
        } else {
            double x = Double.parseDouble(exitX);
            double y = Double.parseDouble(exitY);
            pos.x = x;
            pos.y = y;
        }
        return pos;
    }
}
