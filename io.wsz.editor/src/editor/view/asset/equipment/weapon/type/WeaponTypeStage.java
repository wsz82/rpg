package editor.view.asset.equipment.weapon.type;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class WeaponTypeStage extends ChildStage {
    private static final String TITLE = "Weapon types";

    private final StackPane root = new StackPane();
    private final WeaponTypeListView list;

    public WeaponTypeStage(Stage parent, EditorController editorController) {
        super(parent);
        list = new WeaponTypeListView(editorController);
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        setTitle(TITLE);
        setScene(scene);
        root.getChildren().add(list);
    }
}
