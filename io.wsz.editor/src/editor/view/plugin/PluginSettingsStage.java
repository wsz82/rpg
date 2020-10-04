package editor.view.plugin;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import editor.view.utilities.DoubleField;
import editor.view.utilities.IntegerField;
import editor.view.utilities.ToStringConverter;
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

public class PluginSettingsStage extends ChildStage {
    private final String TITLE = "Plugin settings";
    private final BooleanProperty startingLocation = new SimpleBooleanProperty();
    private final ChoiceBox<Location> locationChoice = new ChoiceBox<>();
    private final DoubleProperty startX = new SimpleDoubleProperty();
    private final DoubleProperty startY = new SimpleDoubleProperty();
    private final IntegerProperty startLevel = new SimpleIntegerProperty();
    private final StackPane root = new StackPane();
    private final EditorController editorController;

    public PluginSettingsStage(Stage parent, EditorController editorController){
        super(parent);
        this.editorController = editorController;
    }

    public void initWindow() {
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
        location.getChildren().addAll(locationLabel, locationChoice);

        final HBox pos = new HBox(10);
        pos.setAlignment(Pos.CENTER_LEFT);
        final Label xLabel = new Label("X:");
        final Label yLabel = new Label("Y:");
        final Label layerLabel = new Label("Layer:");
        final DoubleField inputX = new DoubleField(0.0, false);
        inputX.setPrefWidth(50);
        final DoubleField inputY = new DoubleField(0.0, false);
        inputY.setPrefWidth(50);
        final IntegerField inputLayer = new IntegerField(false);
        inputLayer.setText("0");
        inputLayer.setPrefWidth(50);
        pos.getChildren().addAll(xLabel, inputX, yLabel, inputY, layerLabel, inputLayer);

        v.getChildren().addAll(ifStartingLocation, location, pos);
        root.getChildren().add(v);

        locationChoice.setConverter(new ToStringConverter<>(locationChoice) {
            @Override
            public String toString(Location l) {
                if (l == null) {
                    return "";
                }
                return l.getId();
            }
        });
        locationChoice.setItems(editorController.getObservableLocations());

        locationChoice.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputX.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputY.disableProperty().bind(ifStartingLocation.selectedProperty().not());
        inputLayer.disableProperty().bind(ifStartingLocation.selectedProperty().not());

        StringConverter<Number> stringDoubleConverter = new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return n.toString();
            }

            @Override
            public Number fromString(String s) {
                return Double.parseDouble(s);
            }
        };
        startingLocation.bindBidirectional(ifStartingLocation.selectedProperty());
        Bindings.bindBidirectional(inputX.textProperty(), startX, stringDoubleConverter);
        Bindings.bindBidirectional(inputY.textProperty(), startY, stringDoubleConverter);
        StringConverter<Number> stringIntegerConverter = new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return n.toString();
            }

            @Override
            public Number fromString(String s) {
                try {
                    Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    return 0;
                }
                return Integer.parseInt(s);
            }
        };
        Bindings.bindBidirectional(inputLayer.textProperty(), startLevel, stringIntegerConverter);
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

    public Location getStartLocation() {
        return locationChoice.getValue();
    }

    public void setStartLocation(Location startLocation) {
        locationChoice.setValue(startLocation);
    }

    public double getStartX() {
        return startX.get();
    }

    public void setStartX(double startX) {
        this.startX.set(startX);
    }

    public double getStartY() {
        return startY.get();
    }

    public void setStartY(double startY) {
        this.startY.set(startY);
    }

    public int getStartLevel() {
        return startLevel.get();
    }

    public void setStartLevel(int startLayer) {
        this.startLevel.set(startLayer);
    }
}
