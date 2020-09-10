package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanExpression;
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

    public abstract BooleanExpression<?> getExpression();

    public abstract void populate(BooleanExpression<?> expression);

    public HBox getElements() {
        return elements;
    }
}
