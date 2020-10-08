package io.wsz.model.item.list;

import io.wsz.model.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractItemsList extends AssetList<PosItem<?,?>> {
    private final List<PosItem<?,?>> merged = new ArrayList<>(0);

    public AbstractItemsList() {
    }

    public AbstractItemsList(boolean initLists) {
        if (!initLists) return;
        initLists(initLists);
    }

    protected abstract void initLists(boolean initLists);

    public abstract void fillWith(AbstractItemsList itemsList);

    @Override
    public void add(PosItem<?, ?> item) {
        item.addItemToList(this);
    }

    @Override
    public void remove(PosItem<?,?> item) {
        item.removeItemFromList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains = getLandscapes().contains(equipment);
        if (!contains) contains = getCovers().contains(equipment);
        if (!contains) contains = getCreatures().contains(equipment);
        if (!contains) contains = getTeleports().contains(equipment);
        if (!contains) contains = getInDoors().contains(equipment);
        if (!contains) contains = getOutDoors().contains(equipment);
        if (contains) return true;
        return getEquipment().contains(equipment);
    }

    @Override
    public PosItem<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        PosItem<?, ?> item = getItemFromList(getLandscapes(), itemOrAssetId);
        if (item == null) item = getItemFromList(getCovers(), itemOrAssetId);
        if (item == null) item = getItemFromList(getCreatures(), itemOrAssetId);
        if (item == null) item = getItemFromList(getTeleports(), itemOrAssetId);
        if (item == null) item = getItemFromList(getInDoors(), itemOrAssetId);
        if (item == null) item = getItemFromList(getOutDoors(), itemOrAssetId);
        if (item == null) return getEquipment().getItemByItemOrAssetId(itemOrAssetId);
        return item;
    }

    @Override
    public void forEach(Consumer<? super PosItem<?,?>> action) {
        getLandscapes().forEach(action);
        getCovers().forEach(action);
        getCreatures().forEach(action);
        getTeleports().forEach(action);
        getInDoors().forEach(action);
        getOutDoors().forEach(action);
        getEquipment().forEach(action);
    }

    @Override
    public void clear() {
        getLandscapes().clear();
        getCovers().clear();
        getCreatures().clear();
        getTeleports().clear();
        getInDoors().clear();
        getOutDoors().clear();
        getEquipment().clear();
    }

    @Override
    public List<PosItem<?,?>> getMergedList() {
        merged.clear();
        merged.addAll(getLandscapes());
        merged.addAll(getCovers());
        merged.addAll(getCreatures());
        merged.addAll(getTeleports());
        merged.addAll(getInDoors());
        merged.addAll(getOutDoors());
        merged.addAll(getEquipment().getMergedList());
        return merged;
    }

    public List<Equipment<?,?>> getMergedEquipment() {
        return getEquipment().getMergedList();
    }

    public List<EquipmentMayCountable<?,?>> getMergedEquipmentMayCountable() {
        return getEquipment().getEquipmentMayCountableList().getMergedList();
    }

    public abstract List<Landscape> getLandscapes();

    public abstract List<Cover> getCovers();

    public abstract List<Creature> getCreatures();

    public abstract List<Teleport> getTeleports();

    public abstract List<InDoor> getInDoors();

    public abstract List<OutDoor> getOutDoors();

    public abstract AbstractEquipmentList getEquipment();

    public AbstractEquipmentMayCountableList getEquipmentMayCountableList() {
        return getEquipment().getEquipmentMayCountableList();
    }
}
