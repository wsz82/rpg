package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import javafx.scene.layout.HBox;

public abstract class AfterMethodRequirementView {
    protected final HBox elements = new HBox(5);
    protected final EditorController editorController;

    protected SpecificRequirement specificRequirement;

    public AfterMethodRequirementView(EditorController editorController) {
        this.editorController = editorController;
    }

    public SpecificRequirement getSpecificRequirement() {
        return specificRequirement;
    }

    public abstract HBox getElements();

    public abstract void injectVariables(EditorController editorController, ArgumentType argumentType, String checkingId);

    public abstract void addExpressionTo(Requirements output, Method method);
}
