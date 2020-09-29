package editor.view.dialog.requirement.global;

import editor.model.EditorController;
import editor.view.dialog.requirement.DualChoiceBox;
import io.wsz.model.script.BooleanType;
import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.equals.variable.BooleanTrueFalseGlobalVariable;
import io.wsz.model.script.bool.equals.variable.EqualableTrueFalse;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableBoolean;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.List;

public class GlobalBooleanVariableRequirementView extends GlobalVariableSpecificRequirementView<BooleanTrueFalseGlobalVariable> {
    private final ChoiceBox<EqualsOperator> operatorCB = new ChoiceBox<>();
    private final ChoiceBox<BooleanType> booleanCB = new ChoiceBox<>();
    private final ChoiceBox<VariableBoolean> variableCB = new ChoiceBox<>();
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
        setUpVariableCB(variableCB, editorController.getObservableGlobalBooleans(), previousView.getVariable());
    }

    private void setUpOperatorCB() {
        EqualsOperator[] operators = EqualsOperator.values();
        List<EqualsOperator> types = List.of(operators);
        ObservableList<EqualsOperator> observableTypes = FXCollections.observableArrayList(types);
        operatorCB.setItems(observableTypes);
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
    public BooleanTrueFalseGlobalVariable getExpression() {
        Variable<?> variable = previousView.getVariable();

        String checkingId = null;
        if (variable != null) {
            checkingId = variable.getId();
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
        VariableBoolean value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedId = value.getId();
        }
        EqualableTrueFalse equalable = new EqualableTrueFalse(checkedId, operator, argument);
        return new BooleanTrueFalseGlobalVariable(checkingId, equalable);
    }

    @Override
    public void populate(BooleanTrueFalseGlobalVariable expression) {
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalBooleans().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        EqualableTrueFalse equalable = expression.getEqualable();
        if (equalable == null) return;
        operatorCB.setValue(equalable.getEqualsOperator());
        BooleanType booleanType = BooleanType.FALSE;
        Boolean argument = equalable.getArgument();
        if (argument) {
            booleanType = BooleanType.TRUE;
        }
        booleanCB.setValue(booleanType);
        String checkedId = equalable.getCheckedId();
        editorController.getObservableGlobalBooleans().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }
}
