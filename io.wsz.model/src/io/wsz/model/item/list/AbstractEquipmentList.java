package io.wsz.model.item.list;

import io.wsz.model.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractEquipmentList extends AssetList<Equipment<?,?>> {
    private final List<Equipment<?,?>> mergedEquipment = new ArrayList<>(0);

    public AbstractEquipmentList() {
    }

    public AbstractEquipmentList(boolean initLists) {
        if (!initLists) return;
        initLists(initLists);
    }

    public AbstractEquipmentList(AbstractEquipmentList other, boolean keepId) {
        cloneLists(other, keepId);
    }

    protected abstract void cloneLists(AbstractEquipmentList other, boolean keepId);

    protected abstract void initLists(boolean initLists);

    @Override
    public void clear() {
        getContainers().clear();
        getEquipmentMayCountableList().clear();
    }

    @Override
    public void add(Equipment<?,?> equipment) {
        equipment.addItemToEquipmentList(this);
    }

    @Override
    public void remove(Equipment<?,?> equipment) {
        equipment.removeItemFromEquipmentList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains;
        contains = getContainers().contains(equipment);
        if (contains) return true;
        return getEquipmentMayCountableList().contains(equipment);
    }

    @Override
    public List<Equipment<?,?>> getMergedList() {
        mergedEquipment.clear();
        mergedEquipment.addAll(getContainers());
        mergedEquipment.addAll(getEquipmentMayCountableList().getMergedList());
        return mergedEquipment;
    }

    @Override
    public void forEach(Consumer<? super Equipment<?,?>> action) {
        getContainers().forEach(action);
        getEquipmentMayCountableList().forEach(action);
    }

    @Override
    public Equipment<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        Equipment<?, ?> equipment = getItemFromList(getContainers(), itemOrAssetId);
        if (equipment == null) {
            return getEquipmentMayCountableList().getItemByItemOrAssetId(itemOrAssetId);
        }
        return equipment;
    }

    public abstract List<Container> getContainers();

    public abstract AbstractEquipmentMayCountableList getEquipmentMayCountableList();

    public List<Misc> getMiscs() {
        return getEquipmentMayCountableList().getMiscs();
    }

    public List<Weapon> getWeapons() {
        return getEquipmentMayCountableList().getWeapons();
    }

    public abstract void fillWith(AbstractEquipmentList equipmentList);
}
