package editor.view.dialog;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.BooleanCreatureItemExpression;
import io.wsz.model.script.Method;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.script.ArgumentType.ITEM;
import static io.wsz.model.script.Method.PChas;

public class RequirementsListView {
    private final EditorController editorController;
    private final VBox requirementsBox = new VBox(5);
    private final Button addButton = new Button("Add requirement");
    private final VBox requirementsAndAddButtonBox = new VBox(5, requirementsBox, addButton);
    private final ScrollPane listScrollPane = new ScrollPane(requirementsAndAddButtonBox);
    private final List<RequirementView> requirementViews = new ArrayList<>(0);

    public RequirementsListView(EditorController editorController) {
        this.editorController = editorController;
        hookUpAddEvent();
    }

    private void hookUpAddEvent() {
        addButton.setOnAction(e -> {
            addNewRequirement();
        });
    }

    public void populate(Requirements input) {
        if (input == null) return;
        List<BooleanCreatureItemExpression> booleanPCItemExpressions = input.getBooleanPChasExpressions();
        for (BooleanCreatureItemExpression expression : booleanPCItemExpressions) {
            RequirementView requirementView = new RequirementView(this, editorController);
            requirementViews.add(requirementView);
            requirementsBox.getChildren().add(requirementView.getRow());
            requirementView.setMethod(PChas);

            ArgumentTypeRequirementView typeView = requirementView.getArgumentTypeRequirementView();
            typeView.setArgumentType(ITEM);

            RequirementCreatureItemView creatureItemView = typeView.getRequirementCreatureItemView();
            creatureItemView.populate(expression);
        }
    }

    public Requirements getOutput() {
        Requirements output = new Requirements();
        for (RequirementView requirementView : requirementViews) {
            Method method = requirementView.getMethod();
            if (method == null) continue;
            switch (method) {
                case PChas -> addPChasExpression(output, requirementView);
            }
        }
        if (output.isEmpty()) {
            return null;
        } else {
            return output;
        }
    }

    private void addPChasExpression(Requirements output, RequirementView requirementView) {
        ArgumentTypeRequirementView argumentTypeRequirementView = requirementView.getArgumentTypeRequirementView();
        ArgumentType argumentType = argumentTypeRequirementView.getArgumentType();
        switch (argumentType) {
            case ITEM -> addPChasItemExpression(output, argumentTypeRequirementView);
        }
    }

    private void addPChasItemExpression(Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        RequirementCreatureItemView specificRequirement = argumentTypeRequirementView.getRequirementCreatureItemView(); //TODO class specific req
        BooleanCreatureItemExpression expression = specificRequirement.getExpression();

        List<BooleanCreatureItemExpression> booleanPChasExpressions = output.getBooleanPChasExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNewRequirement() {
        RequirementView requirementView = new RequirementView(this, editorController);
        requirementViews.add(requirementView);
        HBox row = requirementView.getRow();
        requirementsBox.getChildren().add(row);
    }

    public void removeRequirement(RequirementView requirementView) {
        requirementViews.remove(requirementView);
        HBox row = requirementView.getRow();
        requirementsBox.getChildren().remove(row);
    }

    public void clear() {
        requirementsBox.getChildren().clear();
        requirementViews.clear();
    }

    public ScrollPane getListScrollPane() {
        return listScrollPane;
    }
}
