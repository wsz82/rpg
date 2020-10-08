package io.wsz.model.item.list;

import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.item.Misc;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractEquipmentMayCountableList extends AssetList<EquipmentMayCountable<?,?>> {
    private final List<EquipmentMayCountable<?,?>> mergedEquipmentMayCountable = new ArrayList<>(0);

    public AbstractEquipmentMayCountableList() {
    }

    public AbstractEquipmentMayCountableList(boolean initLists) {
        if (!initLists) return;
        initLists();
    }

    public AbstractEquipmentMayCountableList(AbstractEquipmentMayCountableList other, boolean keepId) {
        cloneLists(other, keepId);
    }

    protected abstract void cloneLists(AbstractEquipmentMayCountableList other, boolean keepId);

    protected abstract void initLists();

    @Override
    public void clear() {
        getMiscs().clear();
        getWeapons().clear();
    }

    @Override
    public List<EquipmentMayCountable<?,?>> getMergedList() {
        mergedEquipmentMayCountable.clear();
        mergedEquipmentMayCountable.addAll(getMiscs());
        mergedEquipmentMayCountable.addAll(getWeapons());
        return mergedEquipmentMayCountable;
    }

    @Override
    public void add(EquipmentMayCountable<?, ?> item) {
        item.addItemToEquipmentMayCountableList(this);
    }

    @Override
    public void remove(EquipmentMayCountable<?,?> item) {
        item.removeItemFromEquipmentMayCountableList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains;
        contains = getMiscs().contains(equipment);
        if (contains) return true;
        contains = getWeapons().contains(equipment);
        return contains;
    }

    @Override
    public void forEach(Consumer<? super EquipmentMayCountable<?,?>> action) {
        getMiscs().forEach(action);
        getWeapons().forEach(action);
    }

    @Override
    public EquipmentMayCountable<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        EquipmentMayCountable<?, ?> equipment = getItemFromList(getMiscs(), itemOrAssetId);
        if (equipment == null) return getItemFromList(getWeapons(), itemOrAssetId);
        return equipment;
    }

    public abstract List<Misc> getMiscs();

    public abstract List<Weapon> getWeapons();

    public abstract void fillWith(AbstractEquipmentMayCountableList equipmentMayCountableList);
}
