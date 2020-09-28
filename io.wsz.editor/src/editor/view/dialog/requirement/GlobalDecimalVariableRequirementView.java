package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.DoubleField;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.countable.variable.BooleanDecimalGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableDecimalVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableDecimal;
import javafx.scene.control.ChoiceBox;

public class GlobalDecimalVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final DoubleField argumentInput = new DoubleField(true);
    private final ChoiceBox<VariableDecimal> variableCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalDecimalVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        DualTextFieldChoiceBox<DoubleField> dual = new DualTextFieldChoiceBox<>(argumentInput, variableCB);
        dual.hookUpEvents();
        fillElements();
        setUpOperatorCB(operatorCB);
        setUpVariableCB(variableCB, editorController.getObservableGlobalDecimals(), previousView.getVariable());
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(operatorCB, argumentInput, variableCB);
    }

    @Override
    public BooleanObjectExpression<?> getExpression() {
        Variable<?> variable = previousView.getVariable();

        String checkingId = null;
        if (variable != null) {
            checkingId = variable.getId();
        }

        CompareOperator compareOperator = getCompareOperator();

        Double argument = null;
        if (argumentInput.isVisible()) {
            argument = getArgument();
        }
        String checkedId = null;
        VariableDecimal value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedId = value.getId();
        }

        CountableDecimalVariable countable = new CountableDecimalVariable(checkedId, compareOperator, argument);
        return new BooleanDecimalGlobalVariable(checkingId, countable);
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanDecimalGlobalVariable)) return;
        BooleanDecimalGlobalVariable specificExpression = (BooleanDecimalGlobalVariable) expression;
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalDecimals().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        CountableDecimalVariable countable = specificExpression.getCountable();
        if (countable == null) return;
        setCompareOperator(countable.getCompareOperator());
        setArgument(countable.getArgument());
        String checkedId = countable.getCheckedId();
        editorController.getObservableGlobalDecimals().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }

    public CompareOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(CompareOperator operator) {
        operatorCB.setValue(operator);
    }

    public Double getArgument() {
        return argumentInput.getValue();
    }

    public void setArgument(Double argument) {
        argumentInput.setText(String.valueOf(argument));
    }
}
