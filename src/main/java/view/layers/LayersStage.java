package view.layers;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.layer.Layer;
import model.layer.LayersList;

import java.util.List;

public class LayersStage extends Stage{
    private static final String LAYERS = "Layers";
    private final StackPane root = new StackPane();
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem addLayer = new MenuItem("Add layer");
    private final MenuItem removeLayer = new MenuItem("Remove layers");
    private final Stage parent;
    private final LayersTableView table = new LayersTableView();

    public LayersStage(Stage parent) {
        super(StageStyle.UTILITY);
        this.parent = parent;
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        this.initOwner(parent);
        this.setTitle(LAYERS);
        this.setAlwaysOnTop(true);
        this.setScene(scene);

        setUpContextMenu();

        root.getChildren().add(table);
    }

    private void setUpContextMenu() {
        addLayer.setOnAction(event -> addLayer());
        removeLayer.setOnAction(event -> removeLayer());
        contextMenu.getItems().addAll(addLayer, removeLayer);
        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void addLayer() {
        Layer layer = new Layer("new layer");
        Layer uniqueLayer = getUniqueLayer(layer);
        LayersList.get().add(uniqueLayer);
    }

    private Layer getUniqueLayer(Layer layer) {
        List<Layer> layers = LayersList.get();
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
