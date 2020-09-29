package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.countable.variable.BooleanIntegerGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableIntegerVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableInteger;
import javafx.scene.control.ChoiceBox;

public class GlobalIntegerVariableRequirementView extends SpecificRequirement<BooleanIntegerGlobalVariable> {
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
        setUpVariableCB(variableCB, editorController.getObservableGlobalIntegers(), previousView.getVariable());
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(operatorCB, argumentInput, variableCB);
    }

    @Override
    public BooleanIntegerGlobalVariable getExpression() {
        Variable<?> variable = previousView.getVariable();

        String checkingId = null;
        if (variable != null) {
            checkingId = variable.getId();
        }

        CompareOperator compareOperator = operatorCB.getValue();
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
    public void populate(BooleanIntegerGlobalVariable expression) {
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        CountableIntegerVariable countable = expression.getCountable();
        if (countable == null) return;
        operatorCB.setValue(countable.getCompareOperator());
        argumentInput.setText(String.valueOf(countable.getArgument()));
        String checkedId = countable.getCheckedId();
        editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }
}
