package editor.view.asset.equipment.weapon;

import editor.model.EditorController;
import editor.view.DoubleField;
import editor.view.asset.equipment.EquipmentAssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Weapon;
import io.wsz.model.item.WeaponType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.Optional;

public class WeaponAssetStage extends EquipmentAssetStage<Weapon> {
    private static final String TITLE = "Weapon asset";

    private final ChoiceBox<WeaponType> typeCB = new ChoiceBox<>();
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

        final HBox typeBox = new HBox(10);
        typeBox.setAlignment(Pos.CENTER_LEFT);
        final Label typeLabel = new Label("Weapon type");
        typeBox.getChildren().addAll(typeLabel, typeCB);

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

        container.getChildren().addAll(damageBox, rangeBox, speedBox, typeBox);

        fillInputs();

        hookUpWeaponsEvents();
    }

    private void hookUpWeaponsEvents() {
        ObservableList<WeaponType> weaponTypes = editorController.getObservableWeaponTypes();
        ObservableList<WeaponType> weaponTypesWithNull = FXCollections.observableArrayList(weaponTypes);
        weaponTypesWithNull.add(null);
        typeCB.setItems(weaponTypesWithNull);
        typeCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(WeaponType weaponType) {
                if (weaponType == null) return null;
                return weaponType.getName();
            }

            @Override
            public WeaponType fromString(String name) {
                Optional<WeaponType> optType = editorController.getObservableWeaponTypes().stream()
                        .filter(t -> t.getName().equals(name))
                        .findFirst();
                return optType.orElse(null);
            }
        });
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }

        super.fillInputs();

        WeaponType weaponType = item.getIndividualWeaponType();
        typeCB.setValue(weaponType);

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

        WeaponType weaponType = typeCB.getValue();
        item.setWeaponType(weaponType);

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
        editorController.getObservableAssets().getWeapons().add(asset);
    }

    @Override
    protected Weapon createNewAsset() {
        return new Weapon(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.WEAPON;
    }
}
