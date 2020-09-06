package editor.view.dialog;

import editor.model.EditorController;
import io.wsz.model.script.ArgumentType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class ArgumentTypeRequirementView {
    private final HBox elements = new HBox(5);
    private final ChoiceBox<ArgumentType> argumentTypeCB = new ChoiceBox<>();
    private final HBox elementsWithArgumentTypeCB = new HBox(5, argumentTypeCB, elements);
    private final EditorController editorController;
    private RequirementCreatureItemView requirementCreatureItemView;

    public ArgumentTypeRequirementView(EditorController editorController) {
        this.editorController = editorController;
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
            case ITEM -> setUpCreatureItemRequirement();
        }
    }

    private void setUpCreatureItemRequirement() {
        this.requirementCreatureItemView = new RequirementCreatureItemView(editorController);
        elements.getChildren().addAll(requirementCreatureItemView.getElements());
    }

    public ArgumentType getArgumentType() {
        return argumentTypeCB.getValue();
    }

    public void setArgumentType(ArgumentType argumentType) {
        argumentTypeCB.setValue(argumentType);
    }

    public RequirementCreatureItemView getRequirementCreatureItemView() {
        return requirementCreatureItemView;
    }

    public HBox getElements() {
        return elementsWithArgumentTypeCB;
    }
}
