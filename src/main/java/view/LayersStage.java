package view;

import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Layer;
import model.LayersList;

class LayersStage extends Stage{
    private static final String LAYERS = "Layers";
    private final StackPane root = new StackPane();
    private final ContextMenu contextMenu = new ContextMenu();
    private final MenuItem addLayer = new MenuItem("Add layer");
    private final MenuItem removeLayer = new MenuItem("Remove layer");
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
        fillContextMenu();

        root.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
        addLayer.setOnAction(event -> addLayer());
        removeLayer.setOnAction(event -> removeLayer());
    }

    private void fillContextMenu() {
        contextMenu.getItems().addAll(addLayer, removeLayer);
    }

    private void addLayer() {
        Layer layer = new Layer("new layer");
        LayersList.get().add(layer);
    }

    private void removeLayer() {
        table.remove();
    }
}
