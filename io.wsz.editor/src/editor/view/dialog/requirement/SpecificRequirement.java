package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.variable.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public abstract class SpecificRequirement {
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

    protected <V extends Variable<?>> void setUpVariableCB(ChoiceBox<V> choiceBox, ObservableList<V> variables, Variable<?> toRemove) {
        ObservableList<V> variablesWithNull = FXCollections.observableArrayList(variables);
        variablesWithNull.remove(toRemove);
        variablesWithNull.add(null);
        choiceBox.setItems(variablesWithNull);
    }

    public abstract BooleanObjectExpression<?> getExpression();

    public abstract void populate(BooleanObjectExpression<?> expression);

    public HBox getElements() {
        return elements;
    }
}
