package editor.view.asset;

import editor.view.DoubleField;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.ItemType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class CreatureAssetStage extends AssetStage<Creature> {
    private static final String TITLE = "Creature asset";
    private final ChoiceBox<CreatureSize> sizeCB = new ChoiceBox<>();
    private final ChoiceBox<CreatureControl> controlCB = new ChoiceBox<>();
    private final DoubleField speedInput = new DoubleField(0.0);

    public CreatureAssetStage(Stage parent, Creature asset, boolean isContent){
        super(parent, asset, isContent);
        initWindow();
    }

    public CreatureAssetStage(Stage parent){
        super(parent);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        final HBox sizeBox = new HBox(10);
        final Label sizeLabel = new Label("Size");
        sizeBox.getChildren().addAll(sizeLabel, sizeCB);

        final HBox controlBox = new HBox(10);
        final Label controlLabel = new Label("Control");
        controlBox.getChildren().addAll(controlLabel, controlCB);

        final HBox speedBox = new HBox(10);
        final Label speedLabel = new Label("Speed");
        speedBox.getChildren().addAll(speedLabel, speedInput);

        container.getChildren().addAll(sizeBox, controlBox, speedBox);

        ObservableList<CreatureSize> sizes = FXCollections.observableArrayList();
        sizes.addAll(Arrays.asList(CreatureSize.values()));
        sizeCB.setItems(sizes);
        ObservableList<CreatureControl> controls = FXCollections.observableArrayList();
        controls.addAll(Arrays.asList(CreatureControl.values()));
        controlCB.setItems(controls);

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (asset == null) {
            return;
        }
        CreatureSize size = asset.getSize();
        sizeCB.setValue(size);
        CreatureControl control = asset.getControl();
        controlCB.setValue(control);
        Double speed = asset.getSpeed();
        speedInput.setText(String.valueOf(speed));
    }

    @Override
    protected void defineAsset() {
        asset.setSize(sizeCB.getValue());
        asset.setControl(controlCB.getValue());
        asset.setSpeed(Double.parseDouble(speedInput.getText()));
    }

    @Override
    protected void addAssetToList(Creature asset) {
        ObservableAssets.get().getCreatures().add(asset);
    }

    @Override
    protected Creature createNewAsset(String name, String relativePath) {
        return new Creature(
                null, name, getType(), relativePath,
                true, null, null, null, null);
    }

    @Override
    protected ItemType getType() {
        return ItemType.CREATURE;
    }
}
