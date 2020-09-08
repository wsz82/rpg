package editor.view.script;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GlobalVariablesEditStage extends ChildStage {
    private final EditorController editorController;
    private final GlobalVariablesTableView tableView;

    public GlobalVariablesEditStage(Stage parent, EditorController editorController) {
        super(parent);
        this.editorController = editorController;
        this.tableView = new GlobalVariablesTableView(editorController);
    }

    public void initStage() {
        tableView.initTable();
        StackPane root = new StackPane(tableView);
        Scene scene = new Scene(root);
        setTitle("Global variables");
        setScene(scene);

        hookUpCloseEvent();
    }

    private void hookUpCloseEvent() {
        setOnCloseRequest(e -> {
            tableView.saveVariables();
        });
    }
}
