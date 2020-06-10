package editor.view.asset;

import editor.view.IntegerField;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreatureAssetStage extends AssetStage {
    private static final String TITLE = "Creature asset";
    private final ChoiceBox<CreatureSize> sizeCB = new ChoiceBox<>();
    private final ChoiceBox<CreatureControl> controlCB = new ChoiceBox<>();
    private final IntegerField speedInput = new IntegerField(0);

    public CreatureAssetStage(Stage parent, Asset asset, boolean isContent){
        super(parent, asset, isContent);
        initWindow();
    }

    public CreatureAssetStage(Stage parent, ItemType itemType){
        super(parent, itemType);
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
        Creature cr = (Creature) asset;
        CreatureSize size = cr.getSize();
        sizeCB.setValue(size);
        CreatureControl control = cr.getControl();
        controlCB.setValue(control);
        int speed = cr.getSpeed();
        speedInput.setText(String.valueOf(speed));
    }

    @Override
    protected void bindProperties() {
        super.bindProperties();
        sizeCB.disableProperty()
                .bind(genericCheck.selectedProperty());
        controlCB.disableProperty()
                .bind(genericCheck.selectedProperty());
        speedInput.disableProperty()
                .bind(genericCheck.selectedProperty());
    }

    @Override
    protected void defineAsset() {
        Creature cr = (Creature) asset;
        if (cr.isGeneric()) {
            List<Asset> correspondingAsset = Controller.get().getAssetsList().stream()
                    .filter(a -> a.getName().equals(asset.getName()))
                    .collect(Collectors.toList());
            Creature prototype = (Creature) correspondingAsset.get(0);
            cr.setSize(prototype.getSize());
            cr.setControl(prototype.getControl());
            cr.setSpeed(prototype.getSpeed());
        } else {
            cr.setSize(sizeCB.getValue());
            cr.setControl(controlCB.getValue());
            cr.setSpeed(Integer.parseInt(speedInput.getText()));
        }
    }
}
