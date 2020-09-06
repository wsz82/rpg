package io.wsz.model.script.bool.countable;

import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;

import java.util.List;

public class BooleanPosItemItem extends BooleanCountable<PosItem<?,?>> {
    private static final long serialVersionUID = 1L;

    @Override
    protected long getAmount() {
        if (checkedItem instanceof Creature) {
            Creature cr = (Creature) checkedItem;
            return getCreatureAmount(cr);
        }
        if (checkedItem instanceof Container) {
            Container c = (Container) checkedItem;
            List<Equipment> items = c.getItems();
            return items.stream()
                    .filter(i -> i.getAssetId().equals(itemID))
                    .count();
        }
        return 0;
    }
}
