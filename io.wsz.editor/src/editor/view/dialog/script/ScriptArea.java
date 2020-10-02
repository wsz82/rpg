package editor.view.dialog.script;

import editor.model.EditorController;
import editor.view.script.ScriptEditArea;
import io.wsz.model.script.Script;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ScriptArea {
    private final EditorController editorController;
    private final VBox scriptVBox = new VBox(5);

    private ScriptEditArea scriptEditArea;

    public ScriptArea(EditorController editorController) {
        this.editorController = editorController;
    }

    public void init() {
        final Label scriptAreaLabel = new Label("Script to run on begin");
        scriptAreaLabel.setAlignment(Pos.CENTER);
        scriptEditArea = new ScriptEditArea(editorController);
        scriptEditArea.initEditArea();
        scriptVBox.getChildren().addAll(scriptAreaLabel, scriptEditArea.getEditArea());
    }

    public void clearArea() {
        scriptEditArea.clearArea();
    }

    public void restoreScript(Script script) {
        if (script == null) {
            clearArea();
            return;
        }
        scriptEditArea.fillArea(script);
    }

    public Script getScript() {
        return scriptEditArea.createScript();
    }

    public VBox getScriptContainer() {
        return scriptVBox;
    }
}
