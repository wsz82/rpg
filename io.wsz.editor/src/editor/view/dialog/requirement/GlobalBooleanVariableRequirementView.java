package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.BooleanType;
import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.equals.variable.BooleanTrueFalseGlobalVariable;
import io.wsz.model.script.bool.equals.variable.EqualableTrueFalse;
import io.wsz.model.script.variable.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.List;
import java.util.stream.Collectors;

public class GlobalBooleanVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<EqualsOperator> operatorCB = new ChoiceBox<>();
    private final ChoiceBox<BooleanType> booleanCB = new ChoiceBox<>();
    private final ChoiceBox<Variable<Boolean>> variableCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalBooleanVariableRequirementView(EditorController editorController,
                                                GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        DualChoiceBox dualChoiceBox = new DualChoiceBox(booleanCB, variableCB);
        dualChoiceBox.hookUpEvents();
        fillElements();
        setUpOperatorCB();
        setUpBooleanCB();
        setUpVariableCB();
    }

    private void setUpOperatorCB() {
        EqualsOperator[] operators = EqualsOperator.values();
        List<EqualsOperator> types = List.of(operators);
        ObservableList<EqualsOperator> observableTypes = FXCollections.observableArrayList(types);
        operatorCB.setItems(observableTypes);
    }

    private void setUpVariableCB() {
        List<Variable<Boolean>> observableGlobalVariables = editorController.getObservableGlobalVariables().stream()
                .filter(v -> v.getValue() instanceof Boolean)
                .map(v -> (Variable<Boolean>) v)
                .collect(Collectors.toList());
        ObservableList<Variable<Boolean>> variables = FXCollections.observableArrayList(observableGlobalVariables);
        variables.add(null);
        variableCB.setItems(variables);
    }

    private void setUpBooleanCB() {
        BooleanType[] booleanTypes = BooleanType.values();
        List<BooleanType> types = List.of(booleanTypes);
        ObservableList<BooleanType> observableTypes = FXCollections.observableArrayList(types);
        observableTypes.add(null);
        booleanCB.setItems(observableTypes);
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(operatorCB, booleanCB, variableCB);
    }

    @Override
    public BooleanObjectExpression<?> getExpression() {
        Variable<Boolean> variable = (Variable<Boolean>) previousView.getVariable();

        String checkingId = null;
        if (variable != null) {
            checkingId = variable.getID();
        }

        EqualsOperator operator = operatorCB.getValue();
        Boolean argument = null;
        BooleanType type = booleanCB.getValue();
        if (booleanCB.isVisible() && type != null) {
            argument = switch (type) {
                case TRUE -> true;
                case FALSE -> false;
            };
        }
        String checkedId = null;
        Variable<Boolean> value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedId = value.getID();
        }
        EqualableTrueFalse equalable = new EqualableTrueFalse(checkedId, operator, argument);
        BooleanTrueFalseGlobalVariable expression = new BooleanTrueFalseGlobalVariable(checkingId, equalable);
        return expression;
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanTrueFalseGlobalVariable)) return;
        BooleanTrueFalseGlobalVariable specificExpression = (BooleanTrueFalseGlobalVariable) expression;
        Variable<?> checking = editorController.getObservableGlobalVariables().stream()
                .filter(a -> a.getID().equals(expression.getCheckingId()))
                .findFirst().orElse(null);
        previousView.setVariable(checking);
        EqualableTrueFalse equalable = specificExpression.getEqualable();
        operatorCB.setValue(equalable.getEqualsOperator());

        BooleanType booleanType = null;
        Boolean argument = equalable.getArgument();
        if (argument != null) {
            if (argument) {
                booleanType = BooleanType.TRUE;
            } else {
                booleanType = BooleanType.FALSE;
            }
        }
        if (booleanType != null) {
            booleanCB.setValue(booleanType);
        }

        String checkedId = equalable.getCheckedId();
        Variable<Boolean> checked = editorController.getObservableGlobalVariables().stream()
                .filter(a -> a.getID().equals(checkedId))
                .filter(v -> v.getValue() instanceof Boolean)
                .map(v -> (Variable<Boolean>) v)
                .findFirst().orElse(null);
        if (checked != null) {
            variableCB.setValue(checked);
        }
    }
}
