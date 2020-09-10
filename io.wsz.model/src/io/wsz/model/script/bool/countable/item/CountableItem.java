package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.Countable;

public abstract class CountableItem<A extends Asset> extends Countable<Integer> {
    private static final long serialVersionUID = 1L;

    protected transient BooleanItemExpression<A> expression;

    public CountableItem() {
    }

    public CountableItem(CompareOperator compareOperator, Integer argument, BooleanItemExpression<A> expression) {
        super(compareOperator, argument);
        this.expression = expression;
    }

    @Override
    protected boolean isLesserOrEqual(Integer amount) {
        return amount <= argument;
    }

    @Override
    protected boolean isLesser(Integer amount) {
        return amount < argument;
    }

    @Override
    protected boolean isGreaterOrEqual(Integer amount) {
        return amount >= argument;
    }

    @Override
    protected boolean isGreater(Integer amount) {
        return amount > argument;
    }

    @Override
    protected boolean isNotEqual(Integer amount) {
        return !amount.equals(argument);
    }

    @Override
    protected boolean isEqual(Integer amount) {
        return amount.equals(argument);
    }

    @Override
    public Integer getAmount() {
        return getItemAmount(expression);
    }

    public abstract Integer getItemAmount(BooleanItemExpression<A> expression);

    public void setExpression(BooleanItemExpression<A> expression) {
        this.expression = expression;
    }
}
