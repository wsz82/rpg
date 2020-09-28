package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableBoolean;
import javafx.stage.Stage;

public class GlobalBooleanTableView extends GlobalsTableView<VariableBoolean> {

    public GlobalBooleanTableView(Stage parent, EditorController editorController) {
        super(parent, editorController);
    }

    @Override
    protected VariableBoolean getNewVariable() {
        return new VariableBoolean(false);
    }
}
