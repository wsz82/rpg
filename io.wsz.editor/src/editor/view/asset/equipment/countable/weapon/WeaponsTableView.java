package editor.view.asset.equipment.countable.weapon;

import editor.model.EditorController;
import editor.view.asset.equipment.EquipmentTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.Weapon;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.List;

public class WeaponsTableView extends EquipmentTableView<Weapon> {

    public WeaponsTableView(Stage parent, ObservableList<Weapon> assets,
                     EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initWeaponsTable();
    }

    private void initWeaponsTable() {
        TableColumn<Weapon, Double> damageCol = new TableColumn<>("Damage");
        damageCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return p.getValue().getDamage();
            }
        });
        damageCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        damageCol.setEditable(false);
        getColumns().add(damageCol);

        TableColumn<Weapon, Double> rangeCol = new TableColumn<>("Range");
        rangeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return p.getValue().getRange();
            }
        });
        rangeCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        rangeCol.setEditable(false);
        getColumns().add(rangeCol);

        TableColumn<Weapon, Double> speedCol = new TableColumn<>("Speed");
        speedCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return p.getValue().getSpeed();
            }
        });
        speedCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        speedCol.setEditable(false);
        getColumns().add(speedCol);
    }

    @Override
    protected void editAsset() {
        Weapon w = getSelectionModel().getSelectedItem();
        if (w == null) {
            return;
        }
        WeaponAssetStage as = new WeaponAssetStage(parent, w, false, editorCanvas, editorController);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        WeaponAssetStage as = new WeaponAssetStage(parent, editorCanvas, editorController);
        as.show();
    }

    @Override
    protected List<Weapon> createItems(Coords rawPos) {
        List<Weapon> selectedAssets = getSelectionModel().getSelectedItems();
        List<Weapon> output = new ArrayList<>(1);
        for (Weapon p : selectedAssets) {
            Weapon w = new Weapon(p);
            clonePrototypePos(rawPos, p, w);
            output.add(w);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Weapon> assetsToRemove) {
        editorController.getObservableAssets().getWeapons().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.WEAPON;
    }
}
