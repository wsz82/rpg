package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Weapon;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.ArrayList;
import java.util.List;

public class WeaponsTableView extends AssetsTableView<Weapon> {

    WeaponsTableView(Stage parent, ObservableList<Weapon> assets) {
        super(parent, assets);
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
        WeaponAssetStage as = new WeaponAssetStage(parent, w, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        WeaponAssetStage as = new WeaponAssetStage(parent);
        as.show();
    }

    @Override
    protected List<Weapon> createItems(Coords rawPos, int level) {
        List<Weapon> selectedAssets = getSelectionModel().getSelectedItems();
        List<Weapon> output = new ArrayList<>(1);
        for (Weapon p
                : selectedAssets) {
            Coords pos = rawPos.clonePos();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / Sizes.getMeter();
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Weapon w = new Weapon(
                    p, name, type, path,
                    true);
            w.setPos(pos);
            output.add(w);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Weapon> assetsToRemove) {
        ObservableAssets.get().getWeapons().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.WEAPON;
    }
}
