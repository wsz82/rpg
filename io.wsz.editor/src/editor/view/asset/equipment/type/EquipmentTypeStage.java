package editor.view.asset.equipment.type;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class EquipmentTypeStage extends ChildStage {
    private static final String TITLE = "Equipment types";

    private final StackPane root = new StackPane();
    private final EquipmentTypeListView list;

    public EquipmentTypeStage(Stage parent, EditorController editorController) {
        super(parent);
        list = new EquipmentTypeListView(editorController);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);
        root.getChildren().add(list);
    }
}
