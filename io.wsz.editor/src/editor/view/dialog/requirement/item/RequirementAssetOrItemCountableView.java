package editor.view.dialog.requirement.item;

import editor.model.EditorController;
import editor.view.IntegerField;
import editor.view.dialog.requirement.DualTextFieldChoiceBox;
import editor.view.dialog.requirement.SpecificRequirement;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.Method;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableItem;
import io.wsz.model.script.variable.VariableInteger;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public abstract class RequirementAssetOrItemCountableView<A extends Asset<?>> extends SpecificRequirement<BooleanItemVsItem> {
    private final ChoiceBox<A> assetOrItemCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, true);
    private final ChoiceBox<VariableInteger> variableCB = new ChoiceBox<>();

    public RequirementAssetOrItemCountableView(EditorController editorController) {
        super(editorController);
        DualTextFieldChoiceBox<IntegerField> dual = new DualTextFieldChoiceBox<>(argumentInput, variableCB);
        dual.hookUpEvents();
        fillElements();
        setUpAssetOrItemCB();
        setUpOperatorCB(operatorCB);
        setUpVariableCB(variableCB, editorController.getObservableGlobalIntegers());
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(assetOrItemCB, operatorCB, argumentInput, variableCB);
    }

    private void setUpAssetOrItemCB() {
        ObservableList<A> items = getEquipmentAssetsOrItems();
        assetOrItemCB.setItems(items);
        setUpAssetOrItemCBconverter();
    }

    private void setUpAssetOrItemCBconverter() {
        assetOrItemCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(A item) {
                return getAssetOrItemId(item);
            }

            @Override
            public A fromString(String string) {
                return assetOrItemCB.getValue();
            }
        });
    }

    protected abstract ObservableList<A> getEquipmentAssetsOrItems();

    @Override
    public BooleanItemVsItem getExpression() {
        A assetOrItem = assetOrItemCB.getValue();

        String assetId = null;
        if (assetOrItem != null) {
            assetId = getAssetOrItemId(assetOrItem);
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

    protected abstract String getAssetOrItemId(A assetOrItem);

    @Override
    public void addExpressionTo(Requirements output, Method method) {
        BooleanItemVsItem expression = getExpression();
        switch (method) {
            case PC_HAS -> addExpressionToPChas(output, expression);
            case NPC_HAS -> addExpressionToNPChas(output, expression);
        }
    }

    protected abstract void addExpressionToPChas(Requirements output, BooleanItemVsItem expression);

    protected abstract void addExpressionToNPChas(Requirements output, BooleanItemVsItem expression);

    @Override
    public void populate(BooleanItemVsItem expression) {
        CountableItem countable = expression.getCountable();
        if (countable == null) return;
        String checkedId = countable.getCheckedId();
        getEquipmentAssetsOrItems().stream()
                .filter(a -> getAssetOrItemId(a).equals(checkedId))
                .findFirst().ifPresent(assetOrItemCB::setValue);
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
