package editor.view.asset.equipment.countable;

import editor.model.EditorController;
import editor.view.asset.equipment.EquipmentAssetStage;
import editor.view.stage.EditorCanvas;
import editor.view.utilities.IntegerField;
import io.wsz.model.item.EquipmentMayCountable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public abstract class EquipmentMayCountableAssetStage<A extends EquipmentMayCountable<A,?>> extends EquipmentAssetStage<A> {

    private final CheckBox isCountableCB = new CheckBox("Countable");
    private final IntegerField amountInput = new IntegerField(1, true);
    private HBox amountBox;

    public EquipmentMayCountableAssetStage(Stage parent, A item, boolean isContent,
                                           EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, item, isContent, editorCanvas, editorController);
    }

    public EquipmentMayCountableAssetStage(Stage parent,
                                           EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
    }

    @Override
    protected void initWindow() {
        super.initWindow();

        if (!isContent) {
            container.getChildren().add(isCountableCB);
            setUpIsCountableCB();
        }

        amountBox = new HBox(10);
        final Label amountLabel = new Label("Amount");
        amountBox.getChildren().addAll(amountLabel, amountInput);

        container.getChildren().add(amountBox);
    }

    private void setUpIsCountableCB() {
        isCountableCB.setOnAction(e -> {
            boolean selected = isCountableCB.isSelected();
            amountBox.setVisible(selected);
            if (!selected) {
                amountInput.setText("1");
            }
        });
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (item == null) {
            return;
        }

        if (!isContent) {
            boolean isCountable = item.isCountable();
            isCountableCB.setSelected(isCountable);
            amountBox.setVisible(isCountable);
        }

        Integer amount = item.getIndividualAmount();
        if (amount == null) {
            amountInput.setText(null);
        } else {
            amountInput.setText(String.valueOf(amount));
        }
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();

        if (!isContent) {
            boolean isCountable = isCountableCB.isSelected();
            item.setCountable(isCountable);
        }

        String amount = amountInput.getText();
        if (amount.isEmpty()) {
            if (isContent) {
                item.setAmount(null);
            } else {
                item.setAmount(0);
            }
        } else {
            item.setAmount(Integer.parseInt(amount));
        }
    }
}
