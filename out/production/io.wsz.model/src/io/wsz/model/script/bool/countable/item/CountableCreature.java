package io.wsz.model.script.bool.countable.item;

import io.wsz.model.item.Creature;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanItemExpression;

public class CountableCreature extends CountableItem<Creature> {
    private static final long serialVersionUID = 1L;

    public CountableCreature() {
    }

    public CountableCreature(CompareOperator compareOperator, int argument, BooleanItemExpression<Creature> expression) {
        super(compareOperator, argument, expression);
    }

    @Override
    public Integer getItemAmount(BooleanItemExpression<Creature> expression) {
        Creature checkedItem = expression.getCheckedItem();
        String checkedID = expression.getCheckedID();
        return super.getCreatureAmount(checkedItem, checkedID);
    }
}
