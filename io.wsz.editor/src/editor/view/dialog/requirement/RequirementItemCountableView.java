package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.Countable;
import io.wsz.model.script.bool.countable.item.BooleanCountableItem;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.item.CountableConcreteItem;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.Optional;

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

        String id = null;
        if (item != null) {
            id = item.getAssetId();
        }
        CountableConcreteItem countable = new CountableConcreteItem();
        BooleanItemVsItem expression = new BooleanItemVsItem(id, countable);
        countable.setExpression(expression);

        CompareOperator compareOperator = getCompareOperator();
        if (compareOperator != null) {
            countable.setCompareOperator(compareOperator);
        }
        countable.setArgument(getArgument());
        return expression;
    }

    @Override
    public void populate(BooleanExpression<?> expression) {
        if (!(expression instanceof BooleanCountableItem)) return;
        BooleanCountableItem specificExpression = (BooleanCountableItem) expression;
        Optional<Asset> optAsset = editorController.getObservableAssets().getEquipmentAssets().stream()
                .filter(a -> a.getAssetId().equals(expression.getCheckedID()))
                .findFirst();
        setItem(optAsset.orElse(null));
        Countable<Integer> countable = specificExpression.getCountable();
        setCompareOperator(countable.getCompareOperator());
        setArgument(countable.getArgument());
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
