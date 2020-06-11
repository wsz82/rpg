package editor.view.plugin;

import editor.view.IntegerField;
import editor.view.stage.ChildStage;
import io.wsz.model.Controller;
import io.wsz.model.location.Location;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class PluginSettingsStage extends ChildStage {
    private final String TITLE = "Plugin settings";
    private final BooleanProperty startingLocation = new SimpleBooleanProperty();
    private final ObjectProperty<String> startLocationName = new SimpleObjectProperty<>();
    private final IntegerProperty startX = new SimpleIntegerProperty();
    private final IntegerProperty startY = new SimpleIntegerProperty();
    private final IntegerProperty startLayer = new SimpleIntegerProperty();
    private final StackPane root = new StackPane();

    public PluginSettingsStage(Stage parent){
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);

        final VBox v = new VBox(5);
        v.setPadding(new Insets(10));

        final CheckBox ifStartingLocation = new CheckBox("Mark as game start plugin");
        ifStartingLocation.setAlignment(Pos.CENTER_LEFT);
        ifStartingLocation.setSelected(false);

        final HBox location = new HBox(10);
        location.setAlignment(Pos.CENTER_LEFT);
        final Label locationLabel = new Label("Starting location");
        final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
        location.getChildren().addAll(locationLabel, locationChoice);

        final HBox pos = new HBox(10);
        pos.setAlignment(Pos.CENTER_LEFT);
        final Label xLabel = new Label("X:");
        final Label yLabel = new Label("Y:");
        final Label layerLabel = new Label("Layer:");
        final IntegerField inputX = new IntegerField(0);
        inputX.setPrefWidth(50);
        final IntegerField inputY = new IntegerField(0);
        inputY.setPrefWidth(50);
        final IntegerField inputLayer = new IntegerField();
        inputLayer.setText("0");
        inputLayer.setPrefWidth(50);
        pos.getChildren().addAll(xLabel, inputX, yLabel, inputY, layerLabel, inputLayer);

        v.getChildren().addAll(ifStartingLocation, location, pos);
        root.getChildren().add(v);

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
                List<Location> singleLocation = Controller.get().getLocationsList().stream()
                        .filter(l -> l.getName().equals(s))
                        .collect(Collectors.toList());
                return singleLocation.get(0);
            }
        });
        locationChoice.setItems(Controller.get().getLocationsList());

        locationChoice.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputX.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputY.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputLayer.disableProperty().bind(ifStartingLocation.selectedProperty().not());

        StringConverter<Location> locationConverter = new StringConverter<>() {
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
                List<Location> singleLocation = Controller.get().getLocationsList().stream()
                        .filter(l -> l.getName().equals(s))
                        .collect(Collectors.toList());
                return singleLocation.get(0);
            }
        };
        StringConverter<Number> stringConverter = new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return n.toString();
            }

            @Override
            public Number fromString(String s) {
                return Integer.parseInt(s);
            }
        };
        startingLocation.bindBidirectional(ifStartingLocation.selectedProperty());
        Bindings.bindBidirectional(startLocationName, locationChoice.valueProperty(), locationConverter);
        Bindings.bindBidirectional(inputX.textProperty(), startX, stringConverter);
        Bindings.bindBidirectional(inputY.textProperty(), startY, stringConverter);
        Bindings.bindBidirectional(inputLayer.textProperty(), startLayer, stringConverter);
    }

    public void open(){
        show();
    }

    public boolean isStartingLocation() {
        return startingLocation.get();
    }

    public void setStartingLocation(boolean startingLocation) {
        this.startingLocation.set(startingLocation);
    }

    public String getStartLocationName() {
        return startLocationName.get();
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName.set(startLocationName);
    }

    public int getStartX() {
        return startX.get();
    }

    public void setStartX(int startX) {
        this.startX.set(startX);
    }

    public int getStartY() {
        return startY.get();
    }

    public void setStartY(int startY) {
        this.startY.set(startY);
    }

    public int getStartLayer() {
        return startLayer.get();
    }

    public void setStartLayer(int startLayer) {
        this.startLayer.set(startLayer);
    }
}
