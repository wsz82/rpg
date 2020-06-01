package editor.view.asset;

import editor.view.IntegerField;
import io.wsz.model.item.Asset;
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

public class CreatureAssetStage extends AssetStage {
    private static final String TITLE = "Creature asset";
    private final ChoiceBox<CreatureSize> sizeCB = new ChoiceBox<>();
    private final ChoiceBox<CreatureControl> controlCB = new ChoiceBox<>();
    private final IntegerField speedInput = new IntegerField(0);

    public CreatureAssetStage(Stage parent, Asset asset){
        super(parent, asset);
        initWindow();
    }

    public CreatureAssetStage(Stage parent, ItemType itemType){
        super(parent, itemType);
        initWindow();
    }

    private void initWindow() {
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
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
//        asset.
    }
}
