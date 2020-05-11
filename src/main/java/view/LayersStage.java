package view;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.layer.Layer;
import model.layer.LayersList;

class LayersStage extends Stage{
    private static final String LAYERS = "Layers";
    private final StackPane root = new StackPane();
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem addLayer = new MenuItem("Add layer");
    private final MenuItem removeLayer = new MenuItem("Remove layers");
    private final Stage parent;
    private final LayersTableView table = new LayersTableView();

    LayersStage(Stage parent) {
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
        LayersList.get().add(layer);
    }

    private void removeLayer() {
        table.removeLayers();
    }
}
