package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.dialog.requirement.item.RequirementAssetCountableView;
import editor.view.dialog.requirement.item.RequirementItemHasView;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class ArgumentTypeRequirementView extends AfterMethodRequirementView {
    private final ChoiceBox<ArgumentType> argumentTypeCB = new ChoiceBox<>();
    private final HBox elementsWithArgumentTypeCB = new HBox(5, argumentTypeCB, elements);

    public ArgumentTypeRequirementView(EditorController editorController) {
        super(editorController);
        setUpArgumentTypeCB();
    }

    private void setUpArgumentTypeCB() {
        ArgumentType[] typesArr = ArgumentType.values();
        List<ArgumentType> types = List.of(typesArr);
        ObservableList<ArgumentType> observableTypes = FXCollections.observableArrayList(types);
        argumentTypeCB.setItems(observableTypes);
        argumentTypeCB.valueProperty().addListener((observable, oldType, newType) -> {
            elements.getChildren().clear();
            setUpSpecificRequirement(newType);
        });
    }

    private void setUpSpecificRequirement(ArgumentType newType) {
        specificRequirement = switch (newType) {
            case ASSET -> new RequirementAssetCountableView(editorController);
            case INVENTORY_PLACE -> new RequirementItemHasView(editorController);
            case ITEM -> null; //TODO
        };
        if (specificRequirement == null) return;
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    @Override
    public HBox getElements() {
        return elementsWithArgumentTypeCB;
    }

    @Override
    public void injectVariables(EditorController editorController, ArgumentType argumentType, String checkingId) {
        argumentTypeCB.setValue(argumentType);
    }

    @Override
    public void addExpressionTo(Requirements output, Method method) {
        specificRequirement.addExpressionTo(output, method);
    }

    public ArgumentType getArgumentType() {
        return argumentTypeCB.getValue();
    }

    public void setArgumentType(ArgumentType argumentType) {
        argumentTypeCB.setValue(argumentType);
    }
}
