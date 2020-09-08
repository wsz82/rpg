package editor.view.dialog.script;

import editor.model.EditorController;
import io.wsz.model.script.Script;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ScriptArea {
    private final EditorController editorController;
    private final VBox scriptVBox = new VBox(5);
    private final TextArea textArea = new TextArea();

    public ScriptArea(EditorController editorController) {
        this.editorController = editorController;
    }

    public void init() {
        final Label scriptAreaLabel = new Label("Script to run on begin");
        scriptVBox.getChildren().addAll(scriptAreaLabel, textArea);
    }

    public void clearArea() {
        textArea.setText(null);
    }

    public void restoreScript(Script script) {
        if (script == null) {
            clearArea();
            return;
        }
        textArea.setText(script.getInitialText());
    }

    public Script getScript() {
        String text = textArea.getText();
        return Script.parseScript(text);
    }

    public VBox getScriptArea() {
        return scriptVBox;
    }
}
