package io.wsz.model.item;

import java.util.List;

public interface Containable {
    ItemGetter byItemIdGetter = PosItem::getItemByItemId;
    ItemGetter byAssetIdGetter = PosItem::getItemByItemId;

    List<Equipment> getItems();

    default PosItem getItemByAssetId(PosItem thisItem, String lookedId) {
        String thisId = thisItem.getAssetId();
        return getItemById(byAssetIdGetter, thisItem, lookedId, thisId);
    }

    default PosItem getItemByItemId(PosItem thisItem, String lookedId) {
        String thisId = thisItem.getItemId();
        return getItemById(byItemIdGetter, thisItem, lookedId, thisId);
    }

    private PosItem getItemById(ItemGetter itemGetter, PosItem thisItem, String lookedId, String thisId) {
        if (thisId != null && thisId.equals(lookedId)) {
            return thisItem;
        } else {
            List<Equipment> items = getItems();
            boolean isNotEmpty = !items.isEmpty();
            if (isNotEmpty) {
                for (PosItem i : items) {
                    itemGetter.get(i, lookedId);
                }
            } else {
                return null;
            }
        }
        return null;
    }

    default int getContainableAmountById(String lookedId) {
        List<Equipment> items = getItems();
        boolean isNotEmpty = !items.isEmpty();
        int amount = 0;
        if (isNotEmpty) {
            for (PosItem i : items) {
                amount += i.getAmountById(lookedId);
            }
        }
        return amount;
    }

    @FunctionalInterface
    interface ItemGetter {
        PosItem get(PosItem item, String id);
    }
}
