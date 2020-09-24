package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.BooleanType;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.variable.BooleanTrueFalseGlobalVariable;
import io.wsz.model.script.variable.Variable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

import java.util.List;

public class GlobalBooleanVariableRequirementView extends SpecificRequirement {
    private final ChoiceBox<BooleanType> booleanCB = new ChoiceBox<>();
    private final GlobalVariableRequirementView previousView;

    public GlobalBooleanVariableRequirementView(EditorController editorController,
                                                GlobalVariableRequirementView previousView) {
        super(editorController);
        this.previousView = previousView;
        fillElements();
        setUpBooleanCB(booleanCB);
        setUpDefaultValues();
    }

    private void setUpDefaultValues() {
        booleanCB.setValue(BooleanType.TRUE);
    }

    private void setUpBooleanCB(ChoiceBox<BooleanType> booleanCB) {
        booleanCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(BooleanType type) {
                return type.getDisplay();
            }

            @Override
            public BooleanType fromString(String display) {
                BooleanType[] booleanTypes = BooleanType.values();
                for (BooleanType type : booleanTypes) {
                    if (type.getDisplay().equals(display)) {
                        return type;
                    }
                }
                return BooleanType.FALSE;
            }
        });
        BooleanType[] booleanTypes = BooleanType.values();
        List<BooleanType> types = List.of(booleanTypes);
        ObservableList<BooleanType> observableTypes = FXCollections.observableArrayList(types);
        booleanCB.setItems(observableTypes);
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(booleanCB);
    }

    @Override
    public BooleanExpression<?> getExpression() {
        Variable<Boolean> variable = (Variable<Boolean>) previousView.getVariable();

        String id = null;
        if (variable != null) {
            id = variable.getID();
        }

        BooleanTrueFalseGlobalVariable expression = new BooleanTrueFalseGlobalVariable(id);

        BooleanType type = booleanCB.getValue();
        expression.setBooleanType(type);
        return expression;
    }

    @Override
    public void populate(BooleanExpression<?> expression) {
        if (!(expression instanceof BooleanTrueFalseGlobalVariable)) return;
        BooleanTrueFalseGlobalVariable specificExpression = (BooleanTrueFalseGlobalVariable) expression;
        Variable<?> variable = editorController.getObservableGlobalVariables().stream()
                .filter(a -> a.getID().equals(expression.getCheckedID()))
                .findFirst().orElse(null);
        previousView.setVariable(variable);
        booleanCB.setValue(specificExpression.getBooleanType());
    }
}
