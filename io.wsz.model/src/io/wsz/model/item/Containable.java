package io.wsz.model.item;

import io.wsz.model.item.list.EquipmentList;

import java.util.List;
import java.util.Set;

public interface Containable {
    EquipmentList equipmentToRemove = new EquipmentList(true);
    ItemGetter byItemIdGetter = PosItem::getItemByItemId;
    ItemGetter byAssetIdGetter = PosItem::getItemByAssetId;

    EquipmentList getEquipmentList();

    default PosItem<?,?> getItemByAssetId(PosItem<?,?> thisItem, String lookedId) {
        String thisId = thisItem.getAssetId();
        return getItemById(byAssetIdGetter, thisItem, lookedId, thisId);
    }

    default PosItem<?,?> getItemByItemId(PosItem<?,?> thisItem, String lookedId) {
        String thisId = thisItem.getItemId();
        return getItemById(byItemIdGetter, thisItem, lookedId, thisId);
    }

    private PosItem<?,?> getItemById(ItemGetter itemGetter, PosItem<?,?> thisItem, String lookedId, String thisId) {
        if (thisId != null && thisId.equals(lookedId)) {
            return thisItem;
        } else {
            EquipmentList equipmentList = getEquipmentList();
            for (Equipment<?,?> equipment : equipmentList.getMergedList()) {
                PosItem<?, ?> posItem = itemGetter.get(equipment, lookedId);
                if (posItem != null) return posItem;
            }
            return null;
        }
    }

    default int getContainableAmountById(String lookedId) {
        List<Equipment<?,?>> items = getEquipmentList().getMergedList();
        boolean isNotEmpty = !items.isEmpty();
        int amount = 0;
        if (isNotEmpty) {
            for (PosItem<?,?> i : items) {
                amount += i.getAmountById(lookedId);
            }
        }
        return amount;
    }

    default void addPrototypesToSet(PosItem<?,?> thisItem, Set<PosItem<?,?>> prototypes) {
        prototypes.add(thisItem);
        getEquipmentList().forEach(e -> e.addPrototypeToSet(prototypes));
    }

    default void removeItemsByAssetId(String lookedId) {
        equipmentToRemove.clear();
        EquipmentList equipmentList = getEquipmentList();
        for (Equipment<?,?> equipment : equipmentList.getMergedList()) {
            Equipment<?, ?> item = (Equipment<?, ?>) byAssetIdGetter.get(equipment, lookedId); //TODO correct generic
            if (item != null) equipmentToRemove.add(item);
        }
        equipmentList.removeAll(equipmentToRemove);
    }

    @FunctionalInterface
    interface ItemGetter {
        PosItem<?,?> get(PosItem<?,?> item, String id);
    }
}
