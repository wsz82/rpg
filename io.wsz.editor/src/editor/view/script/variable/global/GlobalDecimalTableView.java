package editor.view.script.variable.global;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableDecimal;

public class GlobalDecimalTableView extends GlobalsTableView<VariableDecimal> {

    public GlobalDecimalTableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected VariableDecimal getNewVariable() {
        return new VariableDecimal(0.0);
    }
}
