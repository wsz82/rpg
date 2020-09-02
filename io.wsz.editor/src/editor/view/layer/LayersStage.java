package editor.view.layer;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;

public class LayersStage extends ChildStage {
    private static final String LAYERS = "Layers";
    private final StackPane root = new StackPane();
    private final LayersTableView table;
    private final EditorController editorController;
    private final Controller controller;

    public LayersStage(Stage parent, ContentTableView contentTableView, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent);
        this.editorController = editorController;
        controller = editorController.getController();
        table = new LayersTableView(contentTableView, editorCanvas, editorController);
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
        controller.getCurrentLocation().getLayers().add(uniqueLayer);
    }

    private Layer getUniqueLayer(Layer layer) {
        List<Layer> layers = controller.getCurrentLocation().getLayers();
        boolean levelExists = layers.stream()
                .anyMatch(l -> l.getLevel() == layer.getLevel());
        boolean nameExists = layers.stream()
                .anyMatch(l -> l.getId().equals(layer.getId()));
        if (levelExists) {
            layer.setLevel(layer.getLevel() - 1);
            return getUniqueLayer(layer);
        } else if (nameExists) {
            layer.setId(layer.getId() + 1);
            return getUniqueLayer(layer);
        }else {
            return layer;
        }
    }

    private void removeLayer() {
        table.removeLayers();
    }
}