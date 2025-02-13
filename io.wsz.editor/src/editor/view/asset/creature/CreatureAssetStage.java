package editor.view.asset.creature;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.asset.coords.CoordsPointEditStage;
import editor.view.asset.coords.PointSetter;
import editor.view.asset.creature.inventory.InventoryItemsStage;
import editor.view.asset.creature.inventory.place.InventoryPlaceEditStage;
import editor.view.stage.EditorCanvas;
import editor.view.utilities.DoubleField;
import editor.view.utilities.IntegerField;
import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureAssetStage extends AssetStage<Creature> {
    private static final String TITLE = "Creature asset";

    private final ChoiceBox<CreatureSize> sizeCB = new ChoiceBox<>();
    private final ChoiceBox<CreatureControl> controlCB = new ChoiceBox<>();
    private final Button middlePointButton = new Button("Middle point");
    private final DoubleField speedInput = new DoubleField(isContent);
    private final DoubleField rangeInput = new DoubleField(isContent);
    private final DoubleField visionRangeInput = new DoubleField(isContent);
    private final IntegerField strengthInput = new IntegerField(isContent);
    private final Button itemsButton = new Button("Items");
    private final Button inventoryPlacesButton = new Button("Inventory places");

    public CreatureAssetStage(Stage parent, Creature asset, boolean isContent,
                              EditorCanvas editorCanvas, EditorController editorController){
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public CreatureAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController){
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        container.getChildren().remove(interactionButton);
        container.getChildren().remove(coverButton);
        container.getChildren().remove(collisionButton);

        final HBox sizeBox = new HBox(10);
        final Label sizeLabel = new Label("Size");
        sizeBox.getChildren().addAll(sizeLabel, sizeCB);

        final HBox controlBox = new HBox(10);
        final Label controlLabel = new Label("Control");
        controlBox.getChildren().addAll(controlLabel, controlCB);

        final HBox speedBox = new HBox(10);
        final Label speedLabel = new Label("Speed");
        speedBox.getChildren().addAll(speedLabel, speedInput);

        final HBox visionRangeBox = new HBox(10);
        final Label visionRangeLabel = new Label("Vision range");
        visionRangeBox.getChildren().addAll(visionRangeLabel, visionRangeInput);

        final HBox rangeBox = new HBox(10);
        final Label rangeLabel = new Label("Range");
        rangeBox.getChildren().addAll(rangeLabel, rangeInput);

        final HBox strengthBox = new HBox(10);
        final Label strengthLabel = new Label("Strength");
        strengthBox.getChildren().addAll(strengthLabel, strengthInput);

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

        container.getChildren().addAll(middlePointButton, sizeBox, controlBox, speedBox, visionRangeBox, rangeBox, strengthBox, itemsButton);

        if (!isContent) {
            container.getChildren().add(inventoryPlacesButton);
            hookUpInventoryPlacesEditEvents();
        }

        hookUpMiddlePointEditEvents();
        hookUpItemsEditEvents();

        fillInputs();
    }

    private void hookUpMiddlePointEditEvents() {
        middlePointButton.setOnAction(e -> {
            openMiddlePointEdit();
        });
    }

    private void openMiddlePointEdit() {
        ResolutionImage initialImage = item.getInitialImage();
        if (initialImage == null) {
            return;
        }
        Image background = initialImage.getFxImage();
        if (background == null) {
            return;
        }
        Coords middlePoint = item.getIndividualMiddlePoint();
        PointSetter pointSetter = item::setMiddlePoint;
        CoordsPointEditStage<Creature> middlePointEdit = new CoordsPointEditStage<>(this, item, middlePoint, background, pointSetter);
        middlePointEdit.initWindow(false, "Middle point edit");
        middlePointEdit.show();
    }

    private void hookUpInventoryPlacesEditEvents() {
        inventoryPlacesButton.setOnAction(e -> {
            if (item.getPath() == null) return;
            Map<InventoryPlaceType, List<Coords>> inventoryPlaces = item.getInventory().getInventoryPlaces();
            if (inventoryPlaces == null) {
                inventoryPlaces = new HashMap<>(0);
            }
            File programDir = controller.getProgramDir();
            CreatureAnimation animation = item.getAnimation();
            ResolutionImage inventoryBasicForEditor = animation.getInventoryBasicForEditor(programDir);
            Image basicInventory = inventoryBasicForEditor.getFxImage();
            InventoryPlaceEditStage editStage =
                    new InventoryPlaceEditStage(controller, parent, item, basicInventory, inventoryPlaces);
            editStage.initWindow(isContent, "Inventory places editor");
            editStage.show();
        });
    }

    private void hookUpItemsEditEvents() {
        itemsButton.setOnAction(e -> {
            InventoryItemsStage itemsStage = new InventoryItemsStage(parent, item, editorCanvas, controller);
            itemsStage.initWindow();
            itemsStage.show();
        });
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();

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

        Double visionRange = item.getIndividualVisionRange();
        if (visionRange == null) {
            visionRangeInput.setText("");
        } else {
            visionRangeInput.setText(String.valueOf(visionRange));
        }

        Double range = item.getIndividualRange();
        if (range == null) {
            rangeInput.setText("");
        } else {
            rangeInput.setText(String.valueOf(range));
        }

        Integer strength = item.getIndividualStrength();
        if (strength == null) {
            strengthInput.setText("");
        } else {
            strengthInput.setText(String.valueOf(strength));
        }
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
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

        String visionRange = visionRangeInput.getText();
        if (visionRange.isEmpty()) {
            if (isContent) {
                item.setVisionRange(null);
            } else {
                item.setVisionRange(1.0);
            }
        } else {
            item.setVisionRange(Double.parseDouble(visionRange));
        }

        String range = rangeInput.getText();
        if (range.isEmpty()) {
            if (isContent) {
                item.setRange(null);
            } else {
                item.setRange(0.0);
            }
        } else {
            item.setRange(Double.parseDouble(range));
        }

        String strength = strengthInput.getText();
        if (strength.isEmpty()) {
            if (isContent) {
                item.setStrength(null);
            } else {
                item.setStrength(0);
            }
        } else {
            item.setStrength(Integer.parseInt(strength));
        }
    }

    @Override
    protected void addAssetToList(Creature asset) {
        controller.getObservableAssets().getCreatures().add(asset);
    }

    @Override
    protected Creature getNewAsset() {
        return new Creature(controller);
    }

}
