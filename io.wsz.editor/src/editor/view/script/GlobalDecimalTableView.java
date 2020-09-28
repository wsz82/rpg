package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.VariableDecimal;
import javafx.stage.Stage;

public class GlobalDecimalTableView extends GlobalsTableView<VariableDecimal> {

    public GlobalDecimalTableView(Stage parent, EditorController editorController) {
        super(parent, editorController);
    }

    @Override
    protected VariableDecimal getNewVariable() {
        return new VariableDecimal(0.0);
    }
}
