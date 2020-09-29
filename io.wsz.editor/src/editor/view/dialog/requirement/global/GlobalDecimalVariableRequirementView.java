package editor.view.dialog.requirement.global;

import editor.model.EditorController;
import editor.view.DoubleField;
import editor.view.dialog.requirement.DualTextFieldChoiceBox;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.countable.variable.BooleanDecimalGlobalVariable;
import io.wsz.model.script.bool.countable.variable.CountableDecimalVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableDecimal;
import javafx.scene.control.ChoiceBox;

public class GlobalDecimalVariableRequirementView extends GlobalVariableSpecificRequirementView<BooleanDecimalGlobalVariable> {
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
    public BooleanDecimalGlobalVariable getExpression() {
        Variable<?> variable = previousView.getVariable();

        String checkingId = null;
        if (variable != null) {
            checkingId = variable.getId();
        }

        CompareOperator compareOperator = operatorCB.getValue();

        Double argument = null;
        if (argumentInput.isVisible()) {
            argument = argumentInput.getValue();
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
    public void populate(BooleanDecimalGlobalVariable expression) {
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalDecimals().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        CountableDecimalVariable countable = expression.getCountable();
        if (countable == null) return;
        operatorCB.setValue(countable.getCompareOperator());
        argumentInput.setText(String.valueOf(countable.getArgument()));
        String checkedId = countable.getCheckedId();
        editorController.getObservableGlobalDecimals().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }
}
