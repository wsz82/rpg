package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableItem;
import io.wsz.model.script.variable.VariableInteger;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class RequirementAssetCountableView extends SpecificRequirement<BooleanItemVsItem> {
    private final ChoiceBox<Asset> assetCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, true);
    private final ChoiceBox<VariableInteger> variableCB = new ChoiceBox<>();

    public RequirementAssetCountableView(EditorController editorController) {
        super(editorController);
        DualTextFieldChoiceBox<IntegerField> dual = new DualTextFieldChoiceBox<>(argumentInput, variableCB);
        dual.hookUpEvents();
        fillElements();
        setUpItemCB();
        setUpOperatorCB(operatorCB);
        setUpVariableCB(variableCB, editorController.getObservableGlobalIntegers());
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(assetCB, operatorCB, argumentInput, variableCB);
    }

    private void setUpItemCB() {
        ObservableList<Asset> assets = editorController.getObservableAssets().getEquipmentAssets();
        assetCB.setItems(assets);
    }

    @Override
    public BooleanItemVsItem getExpression() {
        Asset asset = assetCB.getValue();

        String assetId = null;
        if (asset != null) {
            assetId = asset.getAssetId();
        }
        Integer argument = null;
        if (argumentInput.isVisible()) {
            argument = argumentInput.getValue();
        }
        String checkedVariableId = null;
        VariableInteger value = variableCB.getValue();
        if (variableCB.isVisible() && value != null) {
            checkedVariableId = value.getId();
        }

        CompareOperator compareOperator = operatorCB.getValue();
        CountableItem countable = new CountableItem(assetId, compareOperator, argument, checkedVariableId);
        return new BooleanItemVsItem(null, countable);
    }

    @Override
    public void populate(BooleanItemVsItem expression) {
        CountableItem countable = expression.getCountable();
        if (countable == null) return;
        String checkedId = countable.getCheckedId();
        editorController.getObservableAssets().getEquipmentAssets().stream()
                .filter(a -> a.getAssetId().equals(checkedId))
                .findFirst().ifPresent(assetCB::setValue);
        CompareOperator compareOperator = countable.getCompareOperator();
        if (compareOperator != null) {
            operatorCB.setValue(compareOperator);
        }
        Integer argument = countable.getArgument();
        if (argument != null) {
            argumentInput.setText(String.valueOf(argument));
        }
        String checkedVariableId = countable.getCheckedVariableId();
        editorController.getObservableGlobalIntegers().stream()
                .filter(a -> a.getId().equals(checkedVariableId))
                .findFirst().ifPresent(variableCB::setValue);
    }
}
