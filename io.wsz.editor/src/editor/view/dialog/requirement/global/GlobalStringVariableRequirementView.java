package editor.view.dialog.requirement.global;

import editor.model.EditorController;
import editor.view.dialog.requirement.DualTextFieldChoiceBox;
import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.equals.variable.BooleanStringVariableEquals;
import io.wsz.model.script.bool.equals.variable.EqualableStringVariable;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableString;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.List;

public class GlobalStringVariableRequirementView extends GlobalVariableSpecificRequirementView<BooleanStringVariableEquals> {
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
        setUpVariableCB(variableCB, editorController.getObservableGlobalStrings(), previousView.getVariable());
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
    public BooleanStringVariableEquals getExpression() {
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

        EqualsOperator operator = operatorCB.getValue();
        EqualableStringVariable equalable = new EqualableStringVariable(checkedId, operator, argument);
        return new BooleanStringVariableEquals(checkingId, equalable);
    }

    @Override
    public void populate(BooleanStringVariableEquals expression) {
        String checkingId = expression.getCheckingId();
        editorController.getObservableGlobalStrings().stream()
                .filter(a -> a.getId().equals(checkingId))
                .findFirst().ifPresent(previousView::setVariable);
        EqualableStringVariable equalable = expression.getEqualable();
        if (equalable == null) return;
        operatorCB.setValue(equalable.getEqualsOperator());
        argumentInput.setText(equalable.getArgument());
        String checkedId = equalable.getCheckedId();
        editorController.getObservableGlobalStrings().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(variableCB::setValue);
    }
}