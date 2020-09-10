package editor.view.dialog.requirement;

import editor.model.EditorController;
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
}
