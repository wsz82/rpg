package io.wsz.model.item.list;

import io.wsz.model.item.Misc;
import io.wsz.model.item.Weapon;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class EquipmentMayCountableList extends AbstractEquipmentMayCountableList implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Misc> miscs;
    private List<Weapon> weapons;

    public EquipmentMayCountableList() {
    }

    public EquipmentMayCountableList(boolean initLists) {
        super(initLists);
    }

    public EquipmentMayCountableList(AbstractEquipmentMayCountableList other, boolean keepId) {
        super(other, keepId);
    }

    @Override
    protected void cloneLists(AbstractEquipmentMayCountableList other, boolean keepId) {
        this.miscs = new ArrayList<>(other.getMiscs());
        this.weapons = new ArrayList<>(other.getWeapons());
    }

    @Override
    protected void initLists() {
        this.miscs = new ArrayList<>(0);
        this.weapons = new ArrayList<>(0);
    }

    @Override
    public List<Misc> getMiscs() {
        return miscs;
    }

    @Override
    public List<Weapon> getWeapons() {
        return weapons;
    }

    @Override
    public void fillWith(AbstractEquipmentMayCountableList equipmentMayCountableList) {
        miscs = new ArrayList<>(equipmentMayCountableList.getMiscs());
        weapons = new ArrayList<>(equipmentMayCountableList.getWeapons());
    }

    public void setMiscs(List<Misc> miscs) {
        this.miscs = miscs;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(miscs);
        out.writeObject(weapons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        miscs = (List<Misc>) in.readObject();
        weapons = (List<Weapon>) in.readObject();
    }
}
