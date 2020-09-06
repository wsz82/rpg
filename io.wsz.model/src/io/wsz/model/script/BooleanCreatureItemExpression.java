package io.wsz.model.script;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;

import java.util.List;

public class BooleanCreatureItemExpression extends BooleanCountableExpression<Creature> {
    private static final long serialVersionUID = 4041489417192634892L;

    public BooleanCreatureItemExpression() {}

    public BooleanCreatureItemExpression(CompareOperator compareOperator, String itemID, int argument) {
        super(compareOperator, itemID, argument);
    }

    @Override
    protected long getAmount() {
        List<Equipment> items = checkedItem.getItems();
        return items.stream()
                .filter(i -> i.getAssetId().equals(itemID))
                .count();
    }
}
