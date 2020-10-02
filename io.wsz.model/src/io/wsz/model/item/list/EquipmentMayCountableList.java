package io.wsz.model.item.list;

import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.item.Misc;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.Weapon;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.wsz.model.item.ItemType.MISC;
import static io.wsz.model.item.ItemType.WEAPON;

public class EquipmentMayCountableList extends AssetList<EquipmentMayCountable<?,?>> {
    private static final long serialVersionUID = 1L;

    private final List<EquipmentMayCountable<?,?>> mergedEquipmentMayCountable = new ArrayList<>(0);

    private List<Misc> miscs;
    private List<Weapon> weapons;

    public EquipmentMayCountableList() {
    }

    public EquipmentMayCountableList(boolean initLists) {
        if (!initLists) return;
        this.miscs = new ArrayList<>(0);
        this.weapons = new ArrayList<>(0);
    }

    public EquipmentMayCountableList(EquipmentMayCountableList other) {
        this.miscs = new ArrayList<>(other.miscs);
        this.weapons = new ArrayList<>(other.weapons);
    }

    @Override
    public void clear() {
        miscs.clear();
        weapons.clear();
    }

    public void fillLists(List<EquipmentMayCountable<?,?>> items) {
        List<Weapon> tempW = items.stream()
                .filter(a -> a.getType().equals(WEAPON))
                .map(a -> (Weapon) a)
                .collect(Collectors.toList());
        weapons.addAll(tempW);
        List<Misc> tempMiscs = items.stream()
                .filter(a -> a.getType().equals(MISC))
                .map(a -> (Misc) a)
                .collect(Collectors.toList());
        miscs.addAll(tempMiscs);
    }

    @Override
    public List<EquipmentMayCountable<?,?>> getMergedList() {
        mergedEquipmentMayCountable.clear();
        mergedEquipmentMayCountable.addAll(miscs);
        mergedEquipmentMayCountable.addAll(weapons);
        return mergedEquipmentMayCountable;
    }

    @Override
    public void add(EquipmentMayCountable<?, ?> item) {
        item.addItemToEquipmentMayCountableList(this);
    }

    @Override
    public void remove(EquipmentMayCountable<?, ?> item) {
        item.removeItemFromEquipmentMayCountableList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains;
        contains = miscs.contains(equipment);
        if (contains) return true;
        contains = weapons.contains(equipment);
        return contains;
    }

    @Override
    public void forEach(Consumer<? super EquipmentMayCountable<?,?>> action) {
        miscs.forEach(action);
        weapons.forEach(action);
    }

    @Override
    public EquipmentMayCountable<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        EquipmentMayCountable<?, ?> equipment = getItemFromList(miscs, itemOrAssetId);
        if (equipment == null) return getItemFromList(weapons, itemOrAssetId);
        return equipment;
    }

    public List<Misc> getMiscs() {
        return miscs;
    }

    public List<Weapon> getWeapons() {
        return weapons;
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
