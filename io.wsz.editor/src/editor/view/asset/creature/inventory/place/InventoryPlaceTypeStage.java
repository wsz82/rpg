package editor.view.asset.creature.inventory.place;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class InventoryPlaceTypeStage extends ChildStage {
    private static final String TITLE = "Inventory places types";

    private final StackPane root = new StackPane();
    private final InventoryPlaceTypeListView list;

    public InventoryPlaceTypeStage(Stage parent, EditorController editorController) {
        super(parent);
        list = new InventoryPlaceTypeListView(editorController);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);
        root.getChildren().add(list);
    }
}
