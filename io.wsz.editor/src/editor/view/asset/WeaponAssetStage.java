package editor.view.asset;

import editor.view.DoubleField;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Weapon;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class WeaponAssetStage extends EquipmentAssetStage<Weapon> {
    private static final String TITLE = "Weapon asset";
    private final DoubleField inputDamage = new DoubleField(0.0, isContent);
    private final DoubleField inputRange = new DoubleField(0.0, isContent);
    private final DoubleField inputSpeed = new DoubleField(0.0, isContent);

    public WeaponAssetStage(Stage parent, Weapon asset, boolean isContent) {
        super(parent, asset, isContent);
        initWindow();
    }

    public WeaponAssetStage(Stage parent) {
        super(parent);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        final HBox damage = new HBox(10);
        damage.setAlignment(Pos.CENTER_LEFT);
        final Label damageLabel = new Label("Damage");
        damage.getChildren().addAll(damageLabel, inputDamage);

        final HBox range = new HBox(10);
        range.setAlignment(Pos.CENTER_LEFT);
        final Label rangeLabel = new Label("Range");
        range.getChildren().addAll(rangeLabel, inputRange);

        final HBox speed = new HBox(10);
        speed.setAlignment(Pos.CENTER_LEFT);
        final Label speedLabel = new Label("Speed");
        speed.getChildren().addAll(speedLabel, inputSpeed);

        container.getChildren().addAll(damage, range, speed);

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();

        if (item == null) {
            return;
        }

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
        ObservableAssets.get().getWeapons().add(asset);
    }

    @Override
    protected Weapon createNewAsset(String name, String relativePath) {
        Weapon w = new Weapon(null, name, getType(), relativePath, true, null);
        w.setCoverLine(new ArrayList<>(0));
        w.setCollisionPolygons(new ArrayList<>(0));
        return w;
    }

    @Override
    protected ItemType getType() {
        return ItemType.WEAPON;
    }
}
