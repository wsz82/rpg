package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
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
        AfterMethodRequirementView typeView = requirementView.getAfterMethodRequirementView();
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

    public Requirements getOutput() {
        Requirements output = new Requirements();
        for (RequirementView requirementView : requirementViews) {
            Method method = requirementView.getMethod();
            if (method == null) continue;
            switch (method) {
                case PC_HAS, NPC_HAS -> addItemHasExpression(method, output, requirementView);
                case GLOBAL -> addGlobalVariableExpression(output, requirementView);
            }
        }
        if (output.isEmpty()) {
            return null;
        } else {
            return output;
        }
    }

    private void addGlobalVariableExpression(Requirements output, RequirementView requirementView) {
        GlobalVariableRequirementView secondaryView = (GlobalVariableRequirementView) requirementView.getAfterMethodRequirementView();
        List<BooleanExpression> expressions = output.getGlobalVariablesExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setGlobalVariablesExpressions(expressions);
        }
        SpecificRequirement specificRequirement = secondaryView.getSpecificRequirement();
        BooleanVariableExpression expression = (BooleanVariableExpression) specificRequirement.getExpression();
        expressions.add(expression);
    }

    private void addItemHasExpression(Method method, Requirements output, RequirementView requirementView) {
        ArgumentTypeRequirementView argumentTypeRequirementView = (ArgumentTypeRequirementView) requirementView.getAfterMethodRequirementView();
        ArgumentType argumentType = argumentTypeRequirementView.getArgumentType();
        if (argumentType == null) return;
        switch (method) {
            case PC_HAS -> switchBetweenArgumentTypesPChas(argumentType, output, argumentTypeRequirementView);
            case NPC_HAS -> switchBetweenArgumentTypesNPChas(argumentType, output, argumentTypeRequirementView);
        }
    }

    private void switchBetweenArgumentTypesPChas(ArgumentType argumentType, Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        switch (argumentType) {
            case ASSET -> addPChasItemExpression(output, afterMethodRequirementView);
            case INVENTORY_PLACE -> addPChasInventoryPlaceExpression(output, afterMethodRequirementView);
        }
    }

    private void switchBetweenArgumentTypesNPChas(ArgumentType argumentType, Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        switch (argumentType) {
            case ASSET -> addNPChasItemExpression(output, afterMethodRequirementView);
            case INVENTORY_PLACE -> addNPChasInventoryPlaceExpression(output, afterMethodRequirementView);
        }
    }

    private void addPChasInventoryPlaceExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanExpression> booleanPChasExpressions = output.getBooleanPChasExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasInventoryPlaceExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanExpression> booleanNPChasExpressions = output.getBooleanNPChasExpressions();
        if (booleanNPChasExpressions == null) {
            booleanNPChasExpressions = new ArrayList<>(1);
            output.setBooleanNPChasExpressions(booleanNPChasExpressions);
        }
        booleanNPChasExpressions.add(expression);
    }

    private void addPChasItemExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanItemVsItem expression = (BooleanItemVsItem) specificRequirement.getExpression();

        List<BooleanExpression> booleanPChasExpressions = output.getBooleanPChasItemExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasItemExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasItemExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanExpression expression = specificRequirement.getExpression();

        List<BooleanExpression> booleanNPChasExpressions = output.getBooleanNPChasItemExpressions();
        if (booleanNPChasExpressions == null) {
            booleanNPChasExpressions = new ArrayList<>(1);
            output.setBooleanNPChasItemExpressions(booleanNPChasExpressions);
        }
        booleanNPChasExpressions.add(expression);
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
