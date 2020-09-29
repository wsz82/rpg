package editor.view.dialog.requirement.global;

import editor.model.EditorController;
import editor.view.dialog.requirement.SpecificRequirement;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.BooleanVariableExpression;

import java.util.ArrayList;
import java.util.List;

public abstract class GlobalVariableSpecificRequirementView<B extends BooleanVariableExpression<?>>
        extends SpecificRequirement<B> {

    public GlobalVariableSpecificRequirementView(EditorController editorController) {
        super(editorController);
    }

    @Override
    public void addExpressionTo(Requirements output, Method method) {
        List<BooleanExpression> expressions = output.getGlobalVariablesExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setGlobalVariablesExpressions(expressions);
        }
        B expression = getExpression();
        if (expression == null) return;
        expressions.add(expression);
    }
}
