package io.wsz.model.script.bool.has.item;

import io.wsz.model.item.Creature;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.has.Hasable;

public class HasableCreatureInventoryPlace extends Hasable {
    private static final long serialVersionUID = 1L;

    private transient BooleanItemExpression<Creature> expression;

    public HasableCreatureInventoryPlace() {
    }

    public HasableCreatureInventoryPlace(BooleanItemExpression<Creature> expression) {
        this.expression = expression;
    }

    @Override
    public boolean has() {
        boolean has = expression.getCheckedItem().getInventoryPlaces().keySet().stream()
                .anyMatch(i -> i.getId().equals(expression.getCheckedID()));
        if (not == Not.NOT) {
            return !has;
        } else {
            return has;
        }
    }

    public void setExpression(BooleanItemExpression<Creature> expression) {
        this.expression = expression;
    }
}
