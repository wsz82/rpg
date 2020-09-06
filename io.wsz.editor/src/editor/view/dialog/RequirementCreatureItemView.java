package editor.view.dialog;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.BooleanCountableExpression;
import io.wsz.model.script.BooleanCreatureItemExpression;
import io.wsz.model.script.CompareOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class RequirementCreatureItemView {
    private final HBox elements = new HBox(5);
    private final ChoiceBox<Asset> itemCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, false);
    private final EditorController editorController;

    public RequirementCreatureItemView(EditorController editorController) {
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

    public void populate(BooleanCountableExpression expression) {
        Optional<Asset> optAsset = editorController.getObservableAssets().getEquipmentAssets().stream()
                .filter(a -> a.getAssetId().equals(expression.getItemID()))
                .findFirst();
        setItem(optAsset.orElse(null));
        setCompareOperator(expression.getCompareOperator());
        setArgument(expression.getArgument());
    }
}
