package editor.view.location;

import editor.view.stage.ChildStage;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class LocationsStage extends ChildStage {
    private static final String TITLE = "Locations";
    private final StackPane root = new StackPane();
    private final LocationsTableView table = new LocationsTableView();

    public LocationsStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
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
        Location uniqueLocation = getUniqueLocation(location);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        LocationsList.get().add(uniqueLocation);
    }

    private Location getUniqueLocation(Location location) {
        List<Location> locations = LocationsList.get();
        boolean nameExists = locations.stream()
                .anyMatch(l -> l.getName().equals(location.getName()));
        if (nameExists) {
            location.setName(location.getName() + "New");
            return getUniqueLocation(location);
        } else {
            return location;
        }
    }

    private void removeLocations() {
        table.removeLocations();
    }
}