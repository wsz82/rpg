package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.variable.Variable;
import javafx.collections.FXCollections;
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
        ObservableList<Variable<?>> observableGlobalVariables = editorController.getObservableGlobalVariables();
        ObservableList<Variable<?>> observableGlobalVariablesWithNull = FXCollections.observableArrayList(observableGlobalVariables);
        variableCB.setItems(observableGlobalVariablesWithNull);
        variableCB.valueProperty().addListener((observable, oldVar, newVar) -> {
            elements.getChildren().clear();
            setUpSpecificRequirement(newVar);
        });
    }

    private void setUpSpecificRequirement(Variable<?> newVar) {
        Object value = newVar.getValue();
        if (value instanceof Boolean) {
            setUpBooleanGlobalVariableRequirement();
        } else if (value instanceof Integer) {
            setUpIntegerGlobalVariableRequirement();
        } else if (value instanceof Double) {
            setUpDecimalGlobalVariableRequirement();
        } else if (value instanceof String) {
            setUpStringGlobalVariableRequirement();
        }
    }

    private void setUpBooleanGlobalVariableRequirement() {
        this.specificRequirement = new GlobalBooleanVariableRequirementView(editorController, this);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    private void setUpIntegerGlobalVariableRequirement() {
        this.specificRequirement = new GlobalIntegerVariableRequirementView(editorController, this);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    private void setUpDecimalGlobalVariableRequirement() {
        this.specificRequirement = new GlobalDecimalVariableRequirementView(editorController, this);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    private void setUpStringGlobalVariableRequirement() {
        this.specificRequirement = new GlobalStringVariableRequirementView(editorController, this);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    @Override
    public HBox getElements() {
        return elementsWithVariableCB;
    }

    public Variable<?> getVariable() {
        return variableCB.getValue();
    }

    public void setVariable(Variable<?> asset) {
        variableCB.setValue(asset);
    }
}
