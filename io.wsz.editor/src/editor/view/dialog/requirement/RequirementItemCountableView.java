package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

public class RequirementItemCountableView extends SpecificRequirement {
    private final ChoiceBox<Asset> itemCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, false);

    public RequirementItemCountableView(EditorController editorController) {
        super(editorController);
        setUpItemCB();
        setUpOperatorCB(operatorCB);
        fillElements();
    }

    @Override
    protected void fillElements() {
        elements.getChildren().addAll(itemCB, operatorCB, argumentInput);
    }

    private void setUpItemCB() {
        ObservableList<Asset> assets = editorController.getObservableAssets().getEquipmentAssets();
        itemCB.setItems(assets);
    }

    @Override
    public BooleanItemVsItem getExpression() {
        //TODO generic populate i getExpression
        Asset item = getItem();

        String itemId = null;
        if (item != null) {
            itemId = item.getAssetId();
        }
        CountableItem countable = new CountableItem();
        countable.setCheckedId(itemId);

        CompareOperator compareOperator = getCompareOperator();
        if (compareOperator != null) {
            countable.setCompareOperator(compareOperator);
        }
        countable.setArgument(getArgument());
        BooleanItemVsItem expression = new BooleanItemVsItem(null, countable);
        return expression;
    }

    @Override
    public void populate(BooleanObjectExpression<?> expression) {
        if (!(expression instanceof BooleanItemVsItem)) return;
        BooleanItemVsItem specificExpression = (BooleanItemVsItem) expression;
        CountableItem countable = specificExpression.getCountable();
        Asset asset = editorController.getObservableAssets().getEquipmentAssets().stream()
                .filter(a -> a.getAssetId().equals(countable.getCheckedId()))
                .findFirst().orElse(null);
        setItem(asset);
        CompareOperator compareOperator = countable.getCompareOperator();
        if (compareOperator != null) {
            setCompareOperator(compareOperator);
        }
        Integer argument = countable.getArgument();
        if (argument != null) {
            setArgument(argument);
        }
    }

    public Asset getItem() {
        return itemCB.getValue();
    }

    public void setItem(Asset asset) {
        itemCB.setValue(asset);
    }

    public CompareOperator getCompareOperator() {
        return operatorCB.getValue();
    }

    public void setCompareOperator(CompareOperator operator) {
        operatorCB.setValue(operator);
    }

    public int getArgument() {
        String argument = argumentInput.getText();
        if (argument.isEmpty()) {
            return 0;
        } else {
            return Integer.parseInt(argument);
        }
    }

    public void setArgument(int argument) {
        argumentInput.setText(String.valueOf(argument));
    }
}
