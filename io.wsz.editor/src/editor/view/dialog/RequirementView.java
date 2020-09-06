package editor.view.dialog;

import editor.model.EditorController;
import io.wsz.model.script.Method;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class RequirementView {
    private final EditorController editorController;
    private final HBox elements = new HBox(5);
    private final ChoiceBox<Method> methodCB = new ChoiceBox<>();
    private final HBox elementsWithMethodCB = new HBox(5, methodCB, elements);
    private final Button removeButton = new Button("Remove");
    private final HBox row = new HBox(5, elementsWithMethodCB, removeButton);
    private final RequirementsListView owner;
    private ArgumentTypeRequirementView argumentTypeRequirementView;

    public RequirementView(RequirementsListView owner, EditorController editorController) {
        this.editorController = editorController;
        this.owner = owner;
        setUpMethodCB();
        hookUpRemoveEvent();
    }

    private void setUpMethodCB() {
        Method[] methodsArr = Method.values();
        List<Method> methods = List.of(methodsArr);
        ObservableList<Method> observableMethods = FXCollections.observableArrayList(methods);
        methodCB.setItems(observableMethods);
        methodCB.valueProperty().addListener((observable, oldMethod, newMethod) -> {
            elements.getChildren().clear();
            setUpArgumentTypeRequirement(newMethod);
        });
    }

    private void setUpArgumentTypeRequirement(Method newMethod) {
        switch (newMethod) {
            case PChas -> setUpArgumentTypeRequirement();
        }
    }

    private void setUpArgumentTypeRequirement() {
        this.argumentTypeRequirementView = new ArgumentTypeRequirementView(editorController);
        elements.getChildren().addAll(argumentTypeRequirementView.getElements());
    }

    private void hookUpRemoveEvent() {
        removeButton.setOnAction(e -> {
            removeRequirement();
        });
    }

    private void removeRequirement() {
        owner.removeRequirement(this);
    }


    public HBox getRow() {
        return row;
    }

    public Method getMethod() {
        return methodCB.getValue();
    }

    public void setMethod(Method method) {
        methodCB.setValue(method);
    }

    public ArgumentTypeRequirementView getArgumentTypeRequirementView() {
        return argumentTypeRequirementView;
    }
}
