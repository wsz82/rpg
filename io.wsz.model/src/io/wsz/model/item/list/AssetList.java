package io.wsz.model.item.list;

import io.wsz.model.item.PosItem;

import java.io.Externalizable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AssetList<A extends PosItem<?, ?>> implements Externalizable {

    public abstract void add(A item);

    public abstract void remove(A item);

    public boolean removeById(String itemOrAssetId) {
        A item = getItemByItemOrAssetId(itemOrAssetId);
        if (item != null) {
            remove(item);
            return true;
        } else {
            return false;
        }
    }

    public void removeAll(AssetList<A> list) {
        list.forEach(this::remove);
    }

    public abstract boolean contains(PosItem<?,?> item);

    public abstract A getItemByItemOrAssetId(String itemOrAssetId);

    protected <E extends A> E getItemFromList(List<E> list, String itemOrAssetId) {
        return list.stream()
                .filter(getItemOrAssetIdPredicate(itemOrAssetId))
                .findFirst().orElse(null);
    }

    private <E extends A> Predicate<E> getItemOrAssetIdPredicate(String itemOrAssetId) {
        return i -> {
            String id = i.getItemId();
            if (id != null) {
                boolean equals = id.equals(itemOrAssetId);
                if (equals) {
                    return true;
                } else {
                    return areAssetIdsEqual(i, itemOrAssetId);
                }
            } else {
                return areAssetIdsEqual(i, itemOrAssetId);
            }
        };
    }

    private <E extends A> boolean areAssetIdsEqual(E item, String itemOrAssetId) {
        String id = item.getAssetId();
        return id.equals(itemOrAssetId);
    }

    public abstract void forEach(Consumer<? super A> action);

    public abstract void clear();

    public abstract List<A> getMergedList();
}
