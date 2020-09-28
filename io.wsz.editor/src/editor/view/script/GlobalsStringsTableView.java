package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableString;
import javafx.stage.Stage;

public class GlobalsStringsTableView extends GlobalsTableView<VariableString> {

    public GlobalsStringsTableView(Stage parent, EditorController editorController) {
        super(parent, editorController);
    }

    @Override
    protected VariableString getNewVariable() {
        return new VariableString("");
    }
}
