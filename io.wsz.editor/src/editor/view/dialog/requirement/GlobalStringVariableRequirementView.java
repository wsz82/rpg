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
    private final ChoiceBox<Variable<String>> variableCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalStringVariableRequirementView(EditorController editorController, GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        setUpOperatorCB();
        fillElements();
    }

    private void setUpOperatorCB() {
        EqualsOperator[] operatorsArr = EqualsOperator.values();
        List<EqualsOperator> operators = List.of(operatorsArr);
        ObservableList<EqualsOperator> observableOperators = FXCollections.observableArrayList(operators);
        operatorCB.setItems(observableOperators);
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(operatorCB, argumentInput);
    }

    @Override
    public BooleanObjectExpression<?> getExpression() {
        Variable<String> variable = (Variable<String>) previousView.getVariable();

        String id = null;
        if (variable != null) {
            id = variable.getId();
        }

        EqualableStringVariable equalable = new EqualableStringVariable();
        BooleanStringVariableEquals expression = new BooleanStringVariableEquals(id, equalable);
        equalable.setExpression(expression);

        EqualsOperator equalsOperator = getCompareOperator();
        if (equalsOperator != null) {
            equalable.setEqualsOperator(equalsOperator);
        }
        equalable.setArgument(getArgument());
        return expression;
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanStringVariableEquals)) return;
        BooleanStringVariableEquals specificExpression = (BooleanStringVariableEquals) expression;
        VariableString checking = editorController.getObservableGlobalStrings().stream()
                .filter(a -> a.getId().equals(expression.getCheckingId()))
                .findFirst().orElse(null);
        previousView.setVariable(checking);
        EqualableStringVariable countable = specificExpression.getEqualable();
        setCompareOperator(countable.getEqualsOperator());
        setArgument(countable.getArgument());
    }

    public EqualsOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(EqualsOperator operator) {
        operatorCB.setValue(operator);
    }

    public String getArgument() {
        return argumentInput.getText();
    }

    public void setArgument(String argument) {
        argumentInput.setText(argument);
    }
}