package editor.view.asset.equipment.countable.weapon;

import editor.model.EditorController;
import editor.view.asset.equipment.countable.EquipmentMayCountableAssetStage;
import editor.view.stage.EditorCanvas;
import editor.view.utilities.DoubleField;
import io.wsz.model.item.Weapon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class WeaponAssetStage extends EquipmentMayCountableAssetStage<Weapon> {
    private static final String TITLE = "Weapon asset";

    private final DoubleField inputDamage = new DoubleField(0.0, isContent);
    private final DoubleField inputRange = new DoubleField(0.0, isContent);
    private final DoubleField inputSpeed = new DoubleField(0.0, isContent);

    public WeaponAssetStage(Stage parent, Weapon asset, boolean isContent,
                            EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, asset, isContent, editorCanvas, editorController);
        initWindow();
    }

    public WeaponAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        final HBox damageBox = new HBox(10);
        damageBox.setAlignment(Pos.CENTER_LEFT);
        final Label damageLabel = new Label("Damage");
        damageBox.getChildren().addAll(damageLabel, inputDamage);

        final HBox rangeBox = new HBox(10);
        rangeBox.setAlignment(Pos.CENTER_LEFT);
        final Label rangeLabel = new Label("Range");
        rangeBox.getChildren().addAll(rangeLabel, inputRange);

        final HBox speedBox = new HBox(10);
        speedBox.setAlignment(Pos.CENTER_LEFT);
        final Label speedLabel = new Label("Speed");
        speedBox.getChildren().addAll(speedLabel, inputSpeed);

        container.getChildren().addAll(damageBox, rangeBox, speedBox);

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();

        Double damage = item.getIndividualDamage();
        if (damage == null) {
            inputDamage.setText(null);
        } else {
            inputDamage.setText(String.valueOf(damage));
        }

        Double range = item.getIndividualRange();
        if (range == null) {
            inputRange.setText(null);
        } else {
            inputRange.setText(String.valueOf(range));
        }

        Double speed = item.getIndividualSpeed();
        if (speed == null) {
            inputSpeed.setText(null);
        } else {
            inputSpeed.setText(String.valueOf(speed));
        }
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();

        String damage = inputDamage.getText();
        if (damage.isEmpty()) {
            if (isContent) {
                item.setDamage(null);
            } else {
                item.setDamage(0.0);
            }
        } else {
            item.setDamage(Double.parseDouble(damage));
        }

        String range = inputRange.getText();
        if (range.isEmpty()) {
            if (isContent) {
                item.setRange(null);
            } else {
                item.setRange(0.0);
            }
        } else {
            item.setRange(Double.parseDouble(range));
        }

        String speed = inputSpeed.getText();
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
    protected void addAssetToList(Weapon asset) {
        controller.getObservableAssets().getWeapons().add(asset);
    }

    @Override
    protected Weapon getNewAsset() {
        return new Weapon(controller);
    }

}
