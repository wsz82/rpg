package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanItemExpression;

import java.util.List;

public class CountableConcreteItem extends CountableItem<Asset> {
    private static final long serialVersionUID = 1L;

    public CountableConcreteItem() {
    }

    public CountableConcreteItem(CompareOperator compareOperator, int argument, BooleanItemExpression<Asset> expression) {
        super(compareOperator, argument, expression);
    }

    @Override
    public Integer getItemAmount(BooleanItemExpression<Asset> expression) {
        Asset checkedItem = expression.getCheckedItem();
        String checkedID = expression.getCheckedID();
        if (checkedItem instanceof Creature) {
            Creature cr = (Creature) checkedItem;
            return getCreatureAmount(cr, checkedID);
        }
        if (checkedItem instanceof Container) {
            Container c = (Container) checkedItem;
            List<Equipment> items = c.getItems();
            return (int) items.stream()
                    .filter(i -> i.getAssetId().equals(checkedID))
                    .count();
        }
        return 0;
    }
}
