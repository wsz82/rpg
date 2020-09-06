package editor.view.dialog.requirement;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.BooleanCountable;
import io.wsz.model.script.bool.countable.BooleanCreatureItemExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class RequirementItemCountableView extends SpecificRequirement {
    private final HBox elements = new HBox(5);
    private final ChoiceBox<Asset> itemCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, false);
    private final EditorController editorController;

    public RequirementItemCountableView(EditorController editorController) {
        this.editorController = editorController;
        setUpItemCB();
        setUpOperatorCB();
        fillElements();
    }

    private void fillElements() {
        elements.getChildren().addAll(itemCB, operatorCB, argumentInput);
    }

    private void setUpItemCB() {
        ObservableList<Asset> assets = editorController.getObservableAssets().getEquipmentAssets();
        itemCB.setItems(assets);
    }

    private void setUpOperatorCB() {
        CompareOperator[] operatorsArr = CompareOperator.values();
        List<CompareOperator> operators = List.of(operatorsArr);
        ObservableList<CompareOperator> observableOperators = FXCollections.observableArrayList(operators);
        operatorCB.setItems(observableOperators);
    }

    @Override
    public BooleanCreatureItemExpression getExpression() {
        BooleanCreatureItemExpression expression = new BooleanCreatureItemExpression();
        Asset item = getItem();
        if (item != null) {
            expression.setItemID(item.getAssetId());
        }
        CompareOperator compareOperator = getCompareOperator();
        if (compareOperator != null) {
            expression.setCompareOperator(compareOperator);
        }
        expression.setArgument(getArgument());
        return expression;
    }

    @Override
    public void populate(BooleanExpression expression) {
        if (!(expression instanceof BooleanCountable)) return;
        BooleanCountable specificExpression = (BooleanCountable) expression;
        Optional<Asset> optAsset = editorController.getObservableAssets().getEquipmentAssets().stream()
                .filter(a -> a.getAssetId().equals(expression.getItemID()))
                .findFirst();
        setItem(optAsset.orElse(null));
        setCompareOperator(specificExpression.getCompareOperator());
        setArgument(specificExpression.getArgument());
    }

    @Override
    public HBox getElements() {
        return elements;
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
