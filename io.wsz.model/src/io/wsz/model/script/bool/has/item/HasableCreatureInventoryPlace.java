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

    public HasableCreatureInventoryPlace(String checkedId, Not not) {
        super(checkedId, not);
    }

    @Override
    public boolean has() {
        boolean has = expression.getCheckingObject().getInventoryPlaces().keySet().stream()
                .anyMatch(i -> i.getId().equals(expression.getCheckingId()));
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
