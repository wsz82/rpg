package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableInteger;
import javafx.stage.Stage;

public class GlobalIntegerTableView extends GlobalsTableView<VariableInteger> {

    public GlobalIntegerTableView(Stage parent, EditorController editorController) {
        super(parent, editorController);
    }

    @Override
    protected VariableInteger getNewVariable() {
        return new VariableInteger(0);
    }
}
