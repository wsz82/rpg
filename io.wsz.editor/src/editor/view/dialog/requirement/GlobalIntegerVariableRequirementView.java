package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.countable.variable.BooleanIntegerGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableIntegerVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableInteger;
import javafx.scene.control.ChoiceBox;

public class GlobalIntegerVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(true);
    private final ChoiceBox<VariableInteger> variableCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalIntegerVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        DualTextFieldChoiceBox<IntegerField> dual = new DualTextFieldChoiceBox<>(argumentInput, variableCB);
        dual.hookUpEvents();
        fillElements();
        setUpOperatorCB(operatorCB);
        setUpVariableCB(variableCB, editorController.getObservableGlobalIntegers());
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
        Integer argument = null;
        if (argumentInput.isVisible()) {
            argument = argumentInput.getValue();
        }
        String checkedId = null;
        VariableInteger value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedId = value.getId();
        }

        CountableIntegerVariable countable = new CountableIntegerVariable(checkedId, compareOperator, argument);
        return new BooleanIntegerGlobalVariable(checkingId, countable);
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanIntegerGlobalVariable)) return;
        BooleanIntegerGlobalVariable specificExpression = (BooleanIntegerGlobalVariable) expression;
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        CountableIntegerVariable countable = specificExpression.getCountable();
        if (countable == null) return;
        setCompareOperator(countable.getCompareOperator());
        setArgument(countable.getArgument());
        String checkedId = countable.getCheckedId();
        editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }

    public CompareOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(CompareOperator operator) {
        operatorCB.setValue(operator);
    }

    public Integer getArgument() {
        return argumentInput.getValue();
    }

    public void setArgument(Integer argument) {
        argumentInput.setText(String.valueOf(argument));
    }
}
