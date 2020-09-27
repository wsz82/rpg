package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.variable.BooleanTrueFalseGlobalVariable;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.variable.Variable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.script.ArgumentType.INVENTORY_PLACE;
import static io.wsz.model.script.ArgumentType.ITEM;
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
        List<BooleanItemVsItem> booleanPCItemExpressions = input.getBooleanPChasItemExpressions();
        if (booleanPCItemExpressions != null) {
            for (BooleanItemVsItem expression : booleanPCItemExpressions) {
                populateWithCreatureItemExpression(expression, PC_HAS);
            }
        }
        List<BooleanItemVsItem> booleanNPCItemExpressions = input.getBooleanNPChasItemExpressions();
        if (booleanNPCItemExpressions != null) {
            for (BooleanItemVsItem expression : booleanNPCItemExpressions) {
                populateWithCreatureItemExpression(expression, NPC_HAS);
            }
        }
        List<BooleanItemExpression> booleanPChasExpressions = input.getBooleanPChasExpressions();
        if (booleanPChasExpressions != null) {
            for (BooleanItemExpression expression : booleanPChasExpressions) {
                populateWithCreatureHasInventoryPlaceExpression(expression, PC_HAS);
            }
        }
        List<BooleanItemExpression> booleanNPChasExpressions = input.getBooleanNPChasExpressions();
        if (booleanNPChasExpressions != null) {
            for (BooleanItemExpression expression : booleanNPChasExpressions) {
                populateWithCreatureHasInventoryPlaceExpression(expression, NPC_HAS);
            }
        }

        List<BooleanVariableExpression> globalVariablesExpressions = input.getGlobalVariablesExpressions();
        if (globalVariablesExpressions != null) {
            for (BooleanVariableExpression expression : globalVariablesExpressions) {
                populateWithGlobalVariableExpression(expression, GLOBAL);
            }
        }
    }

    private void populateWithGlobalVariableExpression(BooleanVariableExpression expression, Method method) {
        RequirementView requirementView = getNewRequirementView(method);

        GlobalVariableRequirementView secondaryView = getNewGlobalVariableRequirementView(requirementView, expression);

        SpecificRequirement globalVariableView = secondaryView.getSpecificRequirement();
        globalVariableView.populate(expression);
    }

    private GlobalVariableRequirementView getNewGlobalVariableRequirementView(RequirementView requirementView,
                                                                              BooleanVariableExpression expression) {
        GlobalVariableRequirementView globalVariableView = (GlobalVariableRequirementView) requirementView.getAfterMethodRequirementView();
        Variable<?> variable = editorController.getObservableGlobalVariables().stream()
                .filter(v -> v.getID().equals(expression.getCheckingId()))
                .findFirst()
                .orElse(null);
        globalVariableView.setVariable(variable);
        return globalVariableView;
    }

    private void populateWithCreatureHasInventoryPlaceExpression(BooleanItemExpression expression, Method method) {
        RequirementView requirementView = getNewRequirementView(method);

        AfterMethodRequirementView typeView = getNewArgumentTypeRequirementView(requirementView, INVENTORY_PLACE);

        SpecificRequirement creatureHasView = typeView.getSpecificRequirement();
        creatureHasView.populate(expression);
    }

    private void populateWithCreatureItemExpression(BooleanItemVsItem expression, Method method) {
        RequirementView requirementView = getNewRequirementView(method);

        ArgumentTypeRequirementView typeView = getNewArgumentTypeRequirementView(requirementView, ITEM);

        SpecificRequirement creatureItemView = typeView.getSpecificRequirement();
        creatureItemView.populate(expression);
    }

    private RequirementView getNewRequirementView(Method method) {
        RequirementView requirementView = new RequirementView(this, editorController);
        requirementViews.add(requirementView);
        requirementsBox.getChildren().add(requirementView.getRow());
        requirementView.setMethod(method);
        return requirementView;
    }

    private ArgumentTypeRequirementView getNewArgumentTypeRequirementView(RequirementView requirementView, ArgumentType inventoryPlace) {
        ArgumentTypeRequirementView typeView = (ArgumentTypeRequirementView) requirementView.getAfterMethodRequirementView();
        typeView.setArgumentType(inventoryPlace);
        return typeView;
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
        List<BooleanVariableExpression> expressions = output.getGlobalVariablesExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setGlobalVariablesExpressions(expressions);
        }
        SpecificRequirement specificRequirement = secondaryView.getSpecificRequirement();
        BooleanTrueFalseGlobalVariable expression = (BooleanTrueFalseGlobalVariable) specificRequirement.getExpression();
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
            case ITEM -> addPChasItemExpression(output, afterMethodRequirementView);
            case INVENTORY_PLACE -> addPChasInventoryPlaceExpression(output, afterMethodRequirementView);
        }
    }

    private void switchBetweenArgumentTypesNPChas(ArgumentType argumentType, Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        switch (argumentType) {
            case ITEM -> addNPChasItemExpression(output, afterMethodRequirementView);
            case INVENTORY_PLACE -> addNPChasInventoryPlaceExpression(output, afterMethodRequirementView);
        }
    }

    private void addPChasInventoryPlaceExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanItemExpression> booleanPChasExpressions = output.getBooleanPChasExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasInventoryPlaceExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanItemExpression> booleanNPChasExpressions = output.getBooleanNPChasExpressions();
        if (booleanNPChasExpressions == null) {
            booleanNPChasExpressions = new ArrayList<>(1);
            output.setBooleanNPChasExpressions(booleanNPChasExpressions);
        }
        booleanNPChasExpressions.add(expression);
    }

    private void addPChasItemExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement();
        BooleanItemVsItem expression = (BooleanItemVsItem) specificRequirement.getExpression();

        List<BooleanItemVsItem> booleanPChasExpressions = output.getBooleanPChasItemExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasItemExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasItemExpression(Requirements output, AfterMethodRequirementView afterMethodRequirementView) {
        SpecificRequirement specificRequirement = afterMethodRequirementView.getSpecificRequirement(); //TODO class specific req
        BooleanItemVsItem expression = (BooleanItemVsItem) specificRequirement.getExpression();

        List<BooleanItemVsItem> booleanNPChasExpressions = output.getBooleanNPChasItemExpressions();
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
