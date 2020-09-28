package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.countable.Countable;
import io.wsz.model.script.bool.countable.variable.BooleanIntegerGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableIntegerVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableInteger;
import javafx.scene.control.ChoiceBox;

public class GlobalIntegerVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(false);
    private final GlobalVariableRequirementView previousView;

    public GlobalIntegerVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        setUpOperatorCB(operatorCB);
        fillElements();
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(operatorCB, argumentInput);
    }

    @Override
    public BooleanObjectExpression<?> getExpression() {
        Variable<Integer> variable = (Variable<Integer>) previousView.getVariable();

        String id = null;
        if (variable != null) {
            id = variable.getId();
        }

        CountableIntegerVariable countable = new CountableIntegerVariable();
        BooleanIntegerGlobalVariable expression = new BooleanIntegerGlobalVariable(id, countable);
        countable.setExpression(expression);

        CompareOperator compareOperator = getCompareOperator();
        if (compareOperator != null) {
            countable.setCompareOperator(compareOperator);
        }
        countable.setArgument(getArgument());
        return expression;
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanIntegerGlobalVariable)) return;
        BooleanIntegerGlobalVariable specificExpression = (BooleanIntegerGlobalVariable) expression;
        VariableInteger checking = editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(expression.getCheckingId()))
                .findFirst().orElse(null);
        previousView.setVariable(checking);
        Countable<Integer> countable = specificExpression.getCountable();
        if (countable == null) return;
        setCompareOperator(countable.getCompareOperator());
        setArgument(countable.getArgument());
    }

    public CompareOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(CompareOperator operator) {
        operatorCB.setValue(operator);
    }

    public int getArgument() {
        String argument = argumentInput.getText();
        if (argument.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(argument);
        }
    }

    public void setArgument(int argument) {
        argumentInput.setText(String.valueOf(argument));
    }
}
