package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.Script;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ScriptEditArea {
    private final EditorController editorController;

    private VBox editArea;
    private Button validate;
    private TextArea textArea;
    private Label invalidInfo;

    public ScriptEditArea(EditorController editorController) {
        this.editorController = editorController;
    }

    public void initEditArea() {
        editArea = new VBox(5);
        validate = new Button("Validate");
        HBox topBar = new HBox(5);
        topBar.getChildren().add(validate);
        textArea = new TextArea();
        HBox bottomInfo = new HBox(5);
        invalidInfo = new Label();
        invalidInfo.setTextFill(Color.RED);
        bottomInfo.getChildren().add(invalidInfo);
        editArea.getChildren().addAll(topBar, textArea, bottomInfo);
        editArea.setVisible(false);

        hookUpValidateEvent();
    }

    protected void hookUpValidateEvent() {
        validate.setOnAction(e -> {
            Script script = createScript();
            invalidInfo.setText(script.getValidatorMessage());
        });
    }

    public void fillArea(Script script) {
        editArea.setVisible(true);
        textArea.setText(script.getInitialText());
        invalidInfo.setText(script.getValidatorMessage());
    }

    public void clearArea() {
        editArea.setVisible(false);
        textArea.setText(null);
        invalidInfo.setText(null);
    }

    public Script createScript() {
        return Script.parseScript(textArea.getText(), editorController.getController());
    }

    public void saveScript(Script script) {
        script.fillScript(textArea.getText(), editorController.getController());
        invalidInfo.setText(script.getValidatorMessage());
    }

    public VBox getEditArea() {
        return editArea;
    }
}