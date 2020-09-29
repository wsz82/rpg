package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.variable.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public abstract class SpecificRequirement<B extends BooleanExpression> {
    protected final HBox elements = new HBox(5);
    protected final EditorController editorController;

    public SpecificRequirement(EditorController editorController) {
        this.editorController = editorController;
    }

    protected abstract void fillElements();

    protected void setUpOperatorCB(ChoiceBox<CompareOperator> operatorCB) {
        CompareOperator[] operatorsArr = CompareOperator.values();
        List<CompareOperator> operators = List.of(operatorsArr);
        ObservableList<CompareOperator> observableOperators = FXCollections.observableArrayList(operators);
        operatorCB.setItems(observableOperators);
    }

    protected <V extends Variable<?>> void setUpVariableCB(ChoiceBox<V> choiceBox, ObservableList<V> variables) {
        setUpVariableCB(choiceBox, variables, null);
    }

    protected <V extends Variable<?>> void setUpVariableCB(ChoiceBox<V> choiceBox, ObservableList<V> variables, Variable<?> toRemove) {
        ObservableList<V> variablesWithNull = FXCollections.observableArrayList(variables);
        if (toRemove != null) {
            variablesWithNull.remove(toRemove);
        }
        variablesWithNull.add(null);
        choiceBox.setItems(variablesWithNull);
    }

    public abstract B getExpression();

    public abstract void addExpressionTo(Requirements output, Method method);

    public abstract void populate(B expression);

    public HBox getElements() {
        return elements;
    }
}
