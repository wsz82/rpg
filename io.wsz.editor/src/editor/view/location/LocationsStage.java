package editor.view.location;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class LocationsStage extends ChildStage {
    private static final String TITLE = "Locations";

    private final EditorController editorController;
    private final StackPane root = new StackPane();
    private final LocationsTableView table;

    public LocationsStage(Stage parent, EditorController editorController, EditorCanvas editorCanvas) {
        super(parent);
        this.editorController = editorController;
        this.table = new LocationsTableView(editorCanvas, editorController);
    }

    public void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);
        setUpContextMenu();
        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem addLocation = new MenuItem("Add location");
        final MenuItem removeLocations = new MenuItem("Remove location/s");
        final MenuItem goTo = new MenuItem("Go to");
        addLocation.setOnAction(event -> addLocation());
        removeLocations.setOnAction(event -> removeLocations());
        goTo.setOnAction(event -> goTo());
        contextMenu.getItems().addAll(addLocation, removeLocations, goTo);
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void goTo() {
        table.goTo();
    }

    private void addLocation() {
        Location location = new Location("new", 20, 20);
        List<Location> locations = editorController.getObservableLocations();
        Location uniqueLocation = getUniqueLocation(location, locations);
        Layer layer = new Layer("new");
        location.getLayers().add(layer);
        locations.add(uniqueLocation);
    }

    private Location getUniqueLocation(Location location, List<Location> locations) {
        boolean nameExists = locations.stream()
                .anyMatch(l -> l.getId().equals(location.getId()));
        if (nameExists) {
            location.setId(location.getId() + "New");
            return getUniqueLocation(location, locations);
        } else {
            return location;
        }
    }

    private void removeLocations() {
        table.removeLocations();
    }
}