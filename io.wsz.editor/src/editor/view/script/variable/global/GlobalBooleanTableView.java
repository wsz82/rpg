package editor.view.script.variable.global;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableBoolean;

public class GlobalBooleanTableView extends GlobalsTableView<VariableBoolean> {

    public GlobalBooleanTableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected VariableBoolean getNewVariable() {
        return new VariableBoolean(false);
    }
}
