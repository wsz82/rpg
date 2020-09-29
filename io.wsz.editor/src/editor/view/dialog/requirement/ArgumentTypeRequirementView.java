package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.script.ArgumentType;
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
        switch (newType) {
            case ASSET -> setUpItemCountableRequirement();
            case INVENTORY_PLACE -> setUpItemHasRequirement();
        }
    }

    private void setUpItemCountableRequirement() {
        this.specificRequirement = new RequirementAssetCountableView(editorController);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    private void setUpItemHasRequirement() {
        this.specificRequirement = new RequirementItemHasView(editorController);
        elements.getChildren().addAll(specificRequirement.getElements());
    }

    public ArgumentType getArgumentType() {
        return argumentTypeCB.getValue();
    }

    public void setArgumentType(ArgumentType argumentType) {
        argumentTypeCB.setValue(argumentType);
    }

    @Override
    public HBox getElements() {
        return elementsWithArgumentTypeCB;
    }
}
