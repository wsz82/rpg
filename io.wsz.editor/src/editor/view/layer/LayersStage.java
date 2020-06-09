package editor.view.layer;

import editor.view.stage.ChildStage;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class LayersStage extends ChildStage {
    private static final String LAYERS = "Layers";
    private final StackPane root = new StackPane();
    private final LayersTableView table = new LayersTableView();

    public LayersStage(Stage parent) {
        super(parent);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(LAYERS);
        setScene(scene);
        setUpContextMenu();
        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem addLayer = new MenuItem("Add layer");
        final MenuItem removeLayer = new MenuItem("Remove layer/s");
        final MenuItem changeVisibility = new MenuItem("Change visibility");
        addLayer.setOnAction(event -> addLayer());
        removeLayer.setOnAction(event -> removeLayer());
        changeVisibility.setOnAction(event -> changeVisibility());
        contextMenu.getItems().addAll(addLayer, removeLayer, changeVisibility);
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void changeVisibility() {
        table.changeVisibility();
    }

    private void addLayer() {
        Layer layer = new Layer("new layer");
        Layer uniqueLayer = getUniqueLayer(layer);
        CurrentLocation.get().getLayers().add(uniqueLayer);
    }

    private Layer getUniqueLayer(Layer layer) {
        List<Layer> layers = CurrentLocation.get().getLayers();
        boolean levelExists = layers.stream()
                .anyMatch(l -> l.getLevel() == layer.getLevel());
        boolean nameExists = layers.stream()
                .anyMatch(l -> l.getName().equals(layer.getName()));
        if (levelExists) {
            layer.setLevel(layer.getLevel() - 1);
            return getUniqueLayer(layer);
        } else if (nameExists) {
            layer.setName(layer.getName() + 1);
            return getUniqueLayer(layer);
        }else {
            return layer;
        }
    }

    private void removeLayer() {
        table.removeLayers();
    }
}