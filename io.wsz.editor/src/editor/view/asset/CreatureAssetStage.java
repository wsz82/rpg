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

import java.util.ArrayList;
import java.util.Arrays;

public class CreatureAssetStage extends AssetStage<Creature> {
    private static final String TITLE = "Creature asset";
    private final ChoiceBox<CreatureSize> sizeCB = new ChoiceBox<>();
    private final ChoiceBox<CreatureControl> controlCB = new ChoiceBox<>();
    private final DoubleField speedInput = new DoubleField(true);

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

        if (isContent) {
            sizes.add(null);
            controls.add(null);
        }

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (item == null) {
            return;
        }
        CreatureSize size = item.getIndividualSize();
        sizeCB.setValue(size);
        CreatureControl control = item.getIndividualControl();
        controlCB.setValue(control);
        Double speed = item.getIndividualSpeed();
        if (speed == null) {
            speedInput.setText("");
        } else {
            speedInput.setText(String.valueOf(speed));
        }
    }

    @Override
    protected void defineAsset() {
        CreatureSize size = sizeCB.getValue();
        if (!isContent && size == null) {
            item.setSize(CreatureSize.getDefault());
        } else {
            item.setSize(size);
        }

        CreatureControl control = controlCB.getValue();
        if (!isContent && control == null) {
            item.setControl(CreatureControl.getDefault());
        } else {
            item.setControl(control);
        }

        String speed = speedInput.getText();
        if (speed.isEmpty()) {
            if (isContent) {
                item.setSpeed(null);
            } else {
                item.setSpeed(0.0);
            }
        } else {
            item.setSpeed(Double.parseDouble(speed));
        }
    }

    @Override
    protected void addAssetToList(Creature asset) {
        ObservableAssets.get().getCreatures().add(asset);
    }

    @Override
    protected Creature createNewAsset(String name, String relativePath) {
        return new Creature(
                null, name, getType(), relativePath,
                true, null, null, new ArrayList<>(0), new ArrayList<>(0));
    }

    @Override
    protected ItemType getType() {
        return ItemType.CREATURE;
    }
}
