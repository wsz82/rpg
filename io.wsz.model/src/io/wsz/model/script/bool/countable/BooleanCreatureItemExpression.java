package io.wsz.model.script.bool.countable;

import io.wsz.model.item.Creature;
import io.wsz.model.script.CompareOperator;

public class BooleanCreatureItemExpression extends BooleanCountable<Creature> {
    private static final long serialVersionUID = 1L;

    public BooleanCreatureItemExpression() {}

    public BooleanCreatureItemExpression(CompareOperator compareOperator, String itemID, int argument) {
        super(compareOperator, itemID, argument);
    }

    @Override
    protected long getAmount() {
        return getCreatureAmount(checkedItem);
    }

}
