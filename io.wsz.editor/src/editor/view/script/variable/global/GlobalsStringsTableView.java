package editor.view.script.variable.global;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableString;

public class GlobalsStringsTableView extends GlobalsTableView<VariableString> {

    public GlobalsStringsTableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected VariableString getNewVariable() {
        return new VariableString("");
    }
}
