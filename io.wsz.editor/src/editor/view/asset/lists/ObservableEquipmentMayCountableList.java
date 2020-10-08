package editor.view.asset.lists;

import io.wsz.model.item.Misc;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.Weapon;
import io.wsz.model.item.list.AbstractEquipmentMayCountableList;
import io.wsz.model.item.list.EquipmentMayCountableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ObservableEquipmentMayCountableList extends AbstractEquipmentMayCountableList {
    private ObservableList<Misc> miscs;
    private ObservableList<Weapon> weapons;

    public ObservableEquipmentMayCountableList() {
    }

    public ObservableEquipmentMayCountableList(boolean initLists) {
        super(initLists);
    }

    public ObservableEquipmentMayCountableList(AbstractEquipmentMayCountableList other, boolean keepId) {
        super(other, keepId);
    }

    @Override
    protected void cloneLists(AbstractEquipmentMayCountableList other, boolean keepId) {
        this.miscs = FXCollections.observableArrayList(other.getMiscs());
        this.weapons = FXCollections.observableArrayList(other.getWeapons());
    }

    @Override
    protected void initLists() {
        this.miscs = FXCollections.observableArrayList();
        this.weapons = FXCollections.observableArrayList();
    }

    @Override
    public ObservableList<Misc> getMiscs() {
        return miscs;
    }

    @Override
    public ObservableList<Weapon> getWeapons() {
        return weapons;
    }

    public EquipmentMayCountableList createEquipmentMayCountableList() {
        EquipmentMayCountableList equipmentMayCountableList = new EquipmentMayCountableList();
        equipmentMayCountableList.setMiscs(new ArrayList<>(miscs));
        equipmentMayCountableList.setWeapons(new ArrayList<>(weapons));
        return equipmentMayCountableList;
    }

    public void fillWith(AbstractEquipmentMayCountableList equipmentMayCountableList) {
        clear();
        weapons.addAll(equipmentMayCountableList.getWeapons());
        miscs.addAll(equipmentMayCountableList.getMiscs());
    }

    public void addListener(ListChangeListener<? super PosItem<?,?>> locationListener) {
        weapons.addListener(locationListener);
        miscs.addListener(locationListener);
    }
}
