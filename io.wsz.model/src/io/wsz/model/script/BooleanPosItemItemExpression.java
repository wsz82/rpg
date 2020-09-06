package io.wsz.model.script;

import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;

import java.util.List;

public class BooleanPosItemItemExpression extends BooleanCountableExpression<PosItem<?,?>> {

    @Override
    protected long getAmount() {
        if (checkedItem instanceof Creature) {
            Creature cr = (Creature) checkedItem;
            List<Equipment> items = cr.getItems();
            return items.stream()
                    .filter(i -> i.getAssetId().equals(itemID))
                    .count();
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
