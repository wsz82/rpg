package editor.view.dialog;

import editor.model.EditorController;
import editor.view.IntegerField;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.ArgumentType;
import io.wsz.model.script.CompareOperator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;

public class RequirementCreatureItem {
    private final EditorController editorController;
    private final ChoiceBox<ArgumentType> argumentTypeCB = new ChoiceBox<>();

    private final ChoiceBox<Asset> itemCB = new ChoiceBox<>();
    private final ChoiceBox<CompareOperator> operatorCB = new ChoiceBox<>();
    private final IntegerField argumentInput = new IntegerField(0, false);
    private final HBox elements = new HBox(5, argumentTypeCB, itemCB, operatorCB, argumentInput);

    public RequirementCreatureItem(EditorController editorController) {
        this.editorController = editorController;
        setUpItemCB();
        setUpArgumentTypeCB();
        setUpOperatorCB();
    }

    private void setUpItemCB() {
        ObservableList<Asset> assets = editorController.getObservableAssets().getEquipmentAssets();
        itemCB.setItems(assets);
    }

    private void setUpArgumentTypeCB() {
        ArgumentType[] typesArr = ArgumentType.values();
        List<ArgumentType> types = List.of(typesArr);
        ObservableList<ArgumentType> observableTypes = FXCollections.observableArrayList(types);
        argumentTypeCB.setItems(observableTypes);
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

    public ArgumentType getArgumentType() {
        return argumentTypeCB.getValue();
    }

    public void setArgumentType(ArgumentType argumentType) {
        argumentTypeCB.setValue(argumentType);
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
        if (argument.isEmpty()) return 0;
        return Integer.parseInt(argument);
    }

    public void setArgument(int argument) {

        argumentInput.setText(String.valueOf(argument));
    }
}
