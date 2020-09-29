package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.BooleanExpression;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.script.ArgumentType.ASSET;
import static io.wsz.model.script.ArgumentType.INVENTORY_PLACE;
import static io.wsz.model.script.Method.*;

public class RequirementsListView {
    private final EditorController editorController;
    private final VBox requirementsBox = new VBox(5);
    private final Button addButton = new Button("Add requirement");
    private final VBox requirementsAndAddButtonBox = new VBox(5, requirementsBox, addButton);
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
        List<BooleanExpression> booleanPCItemExpressions = input.getBooleanPChasItemExpressions();
        populateWithExpressions(booleanPCItemExpressions, PC_HAS, ASSET);

        List<BooleanExpression> booleanNPCItemExpressions = input.getBooleanNPChasItemExpressions();
        populateWithExpressions(booleanNPCItemExpressions, NPC_HAS, ASSET);

        List<BooleanExpression> booleanPChasExpressions = input.getBooleanPChasExpressions();
        populateWithExpressions(booleanPChasExpressions, PC_HAS, INVENTORY_PLACE);

        List<BooleanExpression> booleanNPChasExpressions = input.getBooleanNPChasExpressions();
        populateWithExpressions(booleanNPChasExpressions, NPC_HAS, INVENTORY_PLACE);

        List<BooleanExpression> globalVariablesExpressions = input.getGlobalVariablesExpressions();
        populateWithExpressions(globalVariablesExpressions, GLOBAL, null);
    }

    protected <A extends BooleanExpression> void populateWithExpressions(List<A> expressions, Method method, ArgumentType argumentType) {
        if (expressions != null) {
            for (BooleanExpression expression : expressions) {
                populateWithExpression(expression, method, argumentType, expression.getCheckingId());
            }
        }
    }

    private void populateWithExpression(BooleanExpression expression, Method method, ArgumentType argumentType, String checkingId) {
        RequirementView requirementView = createRequirementView(method);
        AfterMethodRequirementView typeView = requirementView.getSecondaryView();
        typeView.injectVariables(editorController, argumentType, checkingId);
        SpecificRequirement specificRequirement = typeView.getSpecificRequirement();
        specificRequirement.populate(expression);
    }

    private RequirementView createRequirementView(Method method) {
        RequirementView requirementView = new RequirementView(this, editorController);
        requirementViews.add(requirementView);
        requirementsBox.getChildren().add(requirementView.getRow());
        requirementView.setMethod(method);
        return requirementView;
    }

    public Requirements createOutput() {
        Requirements output = new Requirements();
        for (RequirementView requirementView : requirementViews) {
            requirementView.addExpressionTo(output);
        }
        if (output.isEmpty()) {
            return null;
        } else {
            return output;
        }
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

    public VBox getContainer() {
        return requirementsAndAddButtonBox;
    }
}
