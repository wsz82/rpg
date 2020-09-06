package editor.view.dialog;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.countable.BooleanCountable;
import io.wsz.model.script.bool.countable.BooleanCreatureItemExpression;
import io.wsz.model.script.bool.has.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.BooleanHas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.script.ArgumentType.INVENTORY_PLACE;
import static io.wsz.model.script.ArgumentType.ITEM;
import static io.wsz.model.script.Method.NPChas;
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
        List<BooleanCountable> booleanPCItemExpressions = input.getBooleanPChasItemExpressions();
        if (booleanPCItemExpressions != null) {
            for (BooleanCountable expression : booleanPCItemExpressions) {
                populateWithCreatureItemExpression(expression, PChas);
            }
        }
        List<BooleanCountable> booleanNPCItemExpressions = input.getBooleanNPChasItemExpressions();
        if (booleanNPCItemExpressions != null) {
            for (BooleanCountable expression : booleanNPCItemExpressions) {
                populateWithCreatureItemExpression(expression, NPChas);
            }
        }
        List<BooleanHas> booleanPChasExpressions = input.getBooleanPChasExpressions();
        if (booleanPChasExpressions != null) {
            for (BooleanHas expression : booleanPChasExpressions) {
                populateWithCreatureHasInventoryPlaceExpression(expression, PChas);
            }
        }
        List<BooleanHas> booleanNPChasExpressions = input.getBooleanNPChasExpressions();
        if (booleanNPChasExpressions != null) {
            for (BooleanHas expression : booleanNPChasExpressions) {
                populateWithCreatureHasInventoryPlaceExpression(expression, NPChas);
            }
        }
    }

    private void populateWithCreatureHasInventoryPlaceExpression(BooleanHas expression, Method method) {
        RequirementView requirementView = getNewRequirementView(method);

        ArgumentTypeRequirementView typeView = getNewArgumentTypeRequirementView(requirementView, INVENTORY_PLACE);

        SpecificRequirement creatureHasView = typeView.getSpecificRequirement();
        creatureHasView.populate(expression);
    }

    private void populateWithCreatureItemExpression(BooleanCountable expression, Method method) {
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
        ArgumentTypeRequirementView typeView = requirementView.getArgumentTypeRequirementView();
        typeView.setArgumentType(inventoryPlace);
        return typeView;
    }

    public Requirements getOutput() {
        Requirements output = new Requirements();
        for (RequirementView requirementView : requirementViews) {
            Method method = requirementView.getMethod();
            if (method == null) continue;
            switch (method) {
                case PChas, NPChas -> addItemHasExpression(method, output, requirementView);
            }
        }
        if (output.isEmpty()) {
            return null;
        } else {
            return output;
        }
    }

    private void addItemHasExpression(Method method, Requirements output, RequirementView requirementView) {
        ArgumentTypeRequirementView argumentTypeRequirementView = requirementView.getArgumentTypeRequirementView();
        ArgumentType argumentType = argumentTypeRequirementView.getArgumentType();
        if (argumentType == null) return;
        switch (method) {
            case PChas -> switchBetweenArgumentTypesPChas(argumentType, output, argumentTypeRequirementView);
            case NPChas -> switchBetweenArgumentTypesNPChas(argumentType, output, argumentTypeRequirementView);
        }
    }

    private void switchBetweenArgumentTypesPChas(ArgumentType argumentType, Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        switch (argumentType) {
            case ITEM -> addPChasItemExpression(output, argumentTypeRequirementView);
            case INVENTORY_PLACE -> addPChasInventoryPlaceExpression(output, argumentTypeRequirementView);
        }
    }

    private void switchBetweenArgumentTypesNPChas(ArgumentType argumentType, Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        switch (argumentType) {
            case ITEM -> addNPChasItemExpression(output, argumentTypeRequirementView);
            case INVENTORY_PLACE -> addNPChasInventoryPlaceExpression(output, argumentTypeRequirementView);
        }
    }

    private void addPChasInventoryPlaceExpression(Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        SpecificRequirement specificRequirement = argumentTypeRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanHas> booleanPChasExpressions = output.getBooleanPChasExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasInventoryPlaceExpression(Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        SpecificRequirement specificRequirement = argumentTypeRequirementView.getSpecificRequirement();
        BooleanCreatureHasInventoryPlace expression = (BooleanCreatureHasInventoryPlace) specificRequirement.getExpression();

        List<BooleanHas> booleanNPChasExpressions = output.getBooleanNPChasExpressions();
        if (booleanNPChasExpressions == null) {
            booleanNPChasExpressions = new ArrayList<>(1);
            output.setBooleanNPChasExpressions(booleanNPChasExpressions);
        }
        booleanNPChasExpressions.add(expression);
    }

    private void addPChasItemExpression(Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        SpecificRequirement specificRequirement = argumentTypeRequirementView.getSpecificRequirement();
        BooleanCreatureItemExpression expression = (BooleanCreatureItemExpression) specificRequirement.getExpression();

        List<BooleanCountable> booleanPChasExpressions = output.getBooleanPChasItemExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasItemExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNPChasItemExpression(Requirements output, ArgumentTypeRequirementView argumentTypeRequirementView) {
        SpecificRequirement specificRequirement = argumentTypeRequirementView.getSpecificRequirement(); //TODO class specific req
        BooleanCreatureItemExpression expression = (BooleanCreatureItemExpression) specificRequirement.getExpression();

        List<BooleanCountable> booleanNPChasExpressions = output.getBooleanNPChasItemExpressions();
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

    public ScrollPane getListScrollPane() {
        return listScrollPane;
    }
}
