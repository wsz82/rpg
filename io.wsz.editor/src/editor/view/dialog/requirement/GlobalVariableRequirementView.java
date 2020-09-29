package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableType;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

public class GlobalVariableRequirementView extends AfterMethodRequirementView {
    private final ChoiceBox<Variable<?>> variableCB = new ChoiceBox<>();
    private final HBox elementsWithVariableCB = new HBox(5, variableCB, elements);

    public GlobalVariableRequirementView(EditorController editorController) {
        super(editorController);
        setUpVariableCB();
    }

    private void setUpVariableCB() {
        ObservableList<Variable<?>> globals = editorController.getObservableGlobals().getMergedVariables();
        variableCB.setItems(globals);
        variableCB.valueProperty().addListener((observable, oldVar, newVar) -> {
            elements.getChildren().clear();
            setUpSpecificRequirement(newVar);
        });
    }

    private void setUpSpecificRequirement(Variable<?> newVar) {
        VariableType type = newVar.getType();
        this.specificRequirement = switch (type) {
            case STRING -> new GlobalStringVariableRequirementView(editorController, this);
            case BOOLEAN -> new GlobalBooleanVariableRequirementView(editorController, this);
            case INTEGER -> new GlobalIntegerVariableRequirementView(editorController, this);
            case DECIMAL -> new GlobalDecimalVariableRequirementView(editorController, this);
        };
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    @Override
    public HBox getElements() {
        return elementsWithVariableCB;
    }

    @Override
    public void injectVariables(EditorController editorController, ArgumentType argumentType, String checkingId) {
        Variable<?> variable = editorController.getObservableGlobals().getMergedVariables().stream()
                .filter(v -> v.getId().equals(checkingId))
                .findFirst().orElse(null);
        setVariable(variable);
    }

    public Variable<?> getVariable() {
        return variableCB.getValue();
    }

    public void setVariable(Variable<?> variable) {
        variableCB.setValue(variable);
    }
}
