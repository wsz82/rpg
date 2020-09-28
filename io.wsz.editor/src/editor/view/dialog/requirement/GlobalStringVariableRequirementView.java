package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.equals.variable.BooleanStringVariableEquals;
import io.wsz.model.script.bool.equals.variable.EqualableStringVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.List;

public class GlobalStringVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<EqualsOperator> operatorCB = new ChoiceBox<>();
    private final TextField argumentInput = new TextField();
    private final ChoiceBox<VariableString> variableCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalStringVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        DualTextFieldChoiceBox<TextField> dual = new DualTextFieldChoiceBox<>(argumentInput, variableCB);
        dual.hookUpEvents();
        fillElements();
        setUpOperatorCB();
        setUpVariableCB(variableCB, editorController.getObservableGlobalStrings());
    }

    private void setUpOperatorCB() {
        EqualsOperator[] operatorsArr = EqualsOperator.values();
        List<EqualsOperator> operators = List.of(operatorsArr);
        ObservableList<EqualsOperator> observableOperators = FXCollections.observableArrayList(operators);
        operatorCB.setItems(observableOperators);
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

        String argument = null;
        if (argumentInput.isVisible()) {
            argument = argumentInput.getText();
        }
        String checkedId = null;
        VariableString value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedId = value.getId();
        }

        EqualsOperator operator = getEqualsOperator();
        EqualableStringVariable equalable = new EqualableStringVariable(checkedId, operator, argument);
        return new BooleanStringVariableEquals(checkingId, equalable);
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanStringVariableEquals)) return;
        BooleanStringVariableEquals specificExpression = (BooleanStringVariableEquals) expression;
        editorController.getObservableGlobalStrings().stream()
                .filter(a -> a.getId().equals(expression.getCheckingId()))
                .findFirst().ifPresent(previousView::setVariable);
        EqualableStringVariable countable = specificExpression.getEqualable();
        setEqualsOperator(countable.getEqualsOperator());
        setArgument(countable.getArgument());
        String checkedId = countable.getCheckedId();
        editorController.getObservableGlobalStrings().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }

    public EqualsOperator getEqualsOperator() {
        return operatorCB.getValue();
    }

    public void setEqualsOperator(EqualsOperator operator) {
        operatorCB.setValue(operator);
    }

    public String getArgument() {
        return argumentInput.getText();
    }

    public void setArgument(String argument) {
        argumentInput.setText(argument);
    }
}