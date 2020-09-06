package editor.view.dialog;

import editor.model.EditorController;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.BooleanCreatureItemExpression;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.Method;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.wsz.model.script.ArgumentType.ITEM;
import static io.wsz.model.script.Method.PChas;

public class RequirementsList {
    private final EditorController editorController;
    private final VBox requirementsBox = new VBox(5);
    private final Button addButton = new Button("Add requirement");
    private final VBox requirementsAndAddButtonBox = new VBox(5, requirementsBox, addButton);
    private final ScrollPane listScrollPane = new ScrollPane(requirementsAndAddButtonBox);
    private final List<Requirement> requirements = new ArrayList<>(0);

    public RequirementsList(EditorController editorController) {
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
            Requirement requirement = new Requirement(this, editorController);
            requirements.add(requirement);
            requirementsBox.getChildren().add(requirement.getRow());
            requirement.setMethod(PChas);
            RequirementCreatureItem specificRequirement = requirement.getSpecificRequirement();
            specificRequirement.setArgumentType(ITEM);
            Optional<Asset> optAsset = editorController.getObservableAssets().getEquipmentAssets().stream()
                    .filter(a -> a.getAssetId().equals(expression.getItemID()))
                    .findFirst();
            specificRequirement.setItem(optAsset.orElse(null));
            specificRequirement.setCompareOperator(expression.getCompareOperator());
            specificRequirement.setArgument(expression.getArgument());
        }
    }

    public Requirements getOutput() {
        Requirements output = new Requirements();
        for (Requirement requirement : requirements) {
            Method method = requirement.getMethod();
            if (method == null) continue;
            switch (method) {
                case PChas -> addPChasExpression(output, requirement);
            }
        }
        if (output.isEmpty()) {
            return null;
        } else {
            return output;
        }
    }

    private void addPChasExpression(Requirements output, Requirement requirement) {
        BooleanCreatureItemExpression expression = new BooleanCreatureItemExpression();
        RequirementCreatureItem specificRequirement = requirement.getSpecificRequirement();
        Asset item = specificRequirement.getItem();
        if (item != null) {
            expression.setItemID(item.getAssetId());
        }
        CompareOperator compareOperator = specificRequirement.getCompareOperator();
        if (compareOperator != null) {
            expression.setCompareOperator(compareOperator);
        }
        expression.setArgument(specificRequirement.getArgument());
        List<BooleanCreatureItemExpression> booleanPChasExpressions = output.getBooleanPChasExpressions();
        if (booleanPChasExpressions == null) {
            booleanPChasExpressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(booleanPChasExpressions);
        }
        booleanPChasExpressions.add(expression);
    }

    private void addNewRequirement() {
        Requirement requirement = new Requirement(this, editorController);
        requirements.add(requirement);
        HBox row = requirement.getRow();
        requirementsBox.getChildren().add(row);
    }

    public void removeRequirement(Requirement requirement) {
        requirements.remove(requirement);
        HBox row = requirement.getRow();
        requirementsBox.getChildren().remove(row);
    }

    public void clear() {
        requirementsBox.getChildren().clear();
        requirements.clear();
    }

    public ScrollPane getListScrollPane() {
        return listScrollPane;
    }
}
