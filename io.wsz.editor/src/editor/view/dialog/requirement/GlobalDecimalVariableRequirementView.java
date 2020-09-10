package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.DoubleField;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.Countable;
import io.wsz.model.script.bool.countable.variable.BooleanDecimalGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableDecimalVariable;
import io.wsz.model.script.variable.Variable;
import javafx.scene.control.ChoiceBox;

import java.util.Optional;

public class GlobalDecimalVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final DoubleField argumentInput = new DoubleField(false);
    private final GlobalVariableRequirementView previousView;

    public GlobalDecimalVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
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
    public BooleanExpression<?> getExpression() {
        Variable<Double> variable = (Variable<Double>) previousView.getVariable();

        String id = null;
        if (variable != null) {
            id = variable.getID();
        }

        CountableDecimalVariable countable = new CountableDecimalVariable();
        BooleanDecimalGlobalVariable expression = new BooleanDecimalGlobalVariable(id, countable);
        countable.setExpression(expression);

        CompareOperator compareOperator = getCompareOperator();
        if (compareOperator != null) {
            countable.setCompareOperator(compareOperator);
        }
        countable.setArgument(getArgument());
        return expression;
    }

    @Override
    public void populate(BooleanExpression<?> expression) {
        if (!(expression instanceof BooleanDecimalGlobalVariable)) return;
        BooleanDecimalGlobalVariable specificExpression = (BooleanDecimalGlobalVariable) expression;
        Optional<Variable<?>> optAsset = editorController.getObservableGlobalVariables().stream()
                .filter(a -> a.getID().equals(expression.getCheckedID()))
                .findFirst();
        previousView.setVariable(optAsset.orElse(null));
        Countable<Double> countable = specificExpression.getCountable();
        setCompareOperator(countable.getCompareOperator());
        setArgument(countable.getArgument());
    }

    public CompareOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(CompareOperator operator) {
        operatorCB.setValue(operator);
    }

    public double getArgument() {
        String argument = argumentInput.getText();
        if (argument.isEmpty()) {
            return 0;
        } else {
            return Double.parseDouble(argument);
        }
    }

    public void setArgument(double argument) {
        argumentInput.setText(String.valueOf(argument));
    }
}
