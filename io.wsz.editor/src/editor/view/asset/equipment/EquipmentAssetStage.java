package editor.view.asset.equipment;

import editor.model.EditorController;
import editor.view.DoubleField;
import editor.view.IntegerField;
import editor.view.asset.AssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.InventoryPlaceType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public abstract class EquipmentAssetStage<A extends Equipment> extends AssetStage<A> {
    protected final ChoiceBox<InventoryPlaceType> occupiedPlaceCB = new ChoiceBox<>();
    protected final DoubleField inputWeight = new DoubleField(0.0, isContent);
    protected final IntegerField inputSize = new IntegerField(0, isContent);

    protected HBox weightBox;

    public EquipmentAssetStage(Stage parent, A item, boolean isContent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, item, isContent, editorCanvas, editorController);
    }

    public EquipmentAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
    }

    @Override
    protected void initWindow() {
        super.initWindow();

        if (!isContent) {
            final HBox occupiedPlaceBox = new HBox(10);
            final Label occupiedPlaceLabel = new Label("Inventory place");
            occupiedPlaceBox.getChildren().addAll(occupiedPlaceLabel, occupiedPlaceCB);
            container.getChildren().add(occupiedPlaceBox);
            setUpOccupiedPlaceCB();
        }

        weightBox = new HBox(10);
        final Label weightLabel = new Label("Weight");
        weightBox.getChildren().addAll(weightLabel, inputWeight);

        final HBox sizeBox = new HBox(10);
        final Label sizeLabel = new Label("Size");
        sizeBox.getChildren().addAll(sizeLabel, inputSize);

        container.getChildren().addAll(weightBox, sizeBox);
    }

    private void setUpOccupiedPlaceCB() {
        occupiedPlaceCB.setPrefWidth(100);
        ObservableList<InventoryPlaceType> origin = editorController.getObservableInventoryPlacesTypes();
        ObservableList<InventoryPlaceType> types = FXCollections.observableArrayList(origin);
        types.add(null);
        occupiedPlaceCB.setItems(types);
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (item == null) {
            return;
        }

        if (!isContent) {
            InventoryPlaceType occupiedPlace = item.getOccupiedPlace();
            occupiedPlaceCB.setValue(occupiedPlace);
        }

        Double weight = item.getIndividualWeight();
        if (weight == null) {
            inputWeight.setText(null);
        } else {
            inputWeight.setText(String.valueOf(weight));
        }

        Integer size = item.getIndividualSize();
        if (size == null) {
            inputSize.setText(null);
        } else {
            inputSize.setText(String.valueOf(size));
        }
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();

        if (!isContent) {
            InventoryPlaceType occupiedPlace = occupiedPlaceCB.getValue();
            item.setOccupiedPlace(occupiedPlace);
        }

        String weight = inputWeight.getText();
        if (weight.isEmpty()) {
            if (isContent) {
                item.setWeight(null);
            } else {
                item.setWeight(0.0);
            }
        } else {
            item.setWeight(Double.parseDouble(weight));
        }

        String size = inputSize.getText();
        if (size.isEmpty()) {
            if (isContent) {
                item.setSize(null);
            } else {
                item.setSize(0);
            }
        } else {
            item.setSize(Integer.parseInt(size));
        }
    }
}
