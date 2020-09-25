package io.wsz.model.item;

import java.util.List;

public interface Containable {
    ItemGetter byItemIdGetter = PosItem::getItemById;
    ItemGetter byAssetIdGetter = PosItem::getItemById;

    List<Equipment> getItems();

    default PosItem getItemByAssetId(PosItem thisItem, String lookedId) {
        String thisId = thisItem.getItemId();
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

    @FunctionalInterface
    interface ItemGetter {
        PosItem get(PosItem item, String id);
    }
}
