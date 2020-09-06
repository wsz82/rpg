package io.wsz.model.script;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;

import java.util.List;

public class BooleanCreatureItemExpression extends BooleanCountableExpression<Creature> {

    public BooleanCreatureItemExpression() {}

    public BooleanCreatureItemExpression(CompareOperator compareOperator, String itemID, int argument) {
        super(compareOperator, itemID, argument);
    }

    @Override
    public boolean isTrue(Creature creature) {
        this.checkedItem = creature;
        return creatureHas();
    }

    private boolean creatureHas() {
        return switch (compareOperator) {
            case EQUAL -> isEqual();
            case NOT_EQUAL -> isNotEqual();
            case GREATER -> isGreater();
            case GREATER_OR_EQUAL -> isGreaterOrEqual();
            case LESSER -> isLesser();
            case LESSER_OR_EQUAL -> isLesserOrEqual();
        };
    }

    @Override
    protected long getAmount() {
        List<Equipment> items = checkedItem.getItems();
        return items.stream()
                .filter(i -> i.getAssetId().equals(itemID))
                .count();
    }
}
