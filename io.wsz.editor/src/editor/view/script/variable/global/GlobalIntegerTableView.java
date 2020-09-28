package editor.view.script.variable.global;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableInteger;

public class GlobalIntegerTableView extends GlobalsTableView<VariableInteger> {

    public GlobalIntegerTableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected VariableInteger getNewVariable() {
        return new VariableInteger(0);
    }
}
