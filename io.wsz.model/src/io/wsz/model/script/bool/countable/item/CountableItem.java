package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.Countable;

public class CountableItem extends Countable<Integer> {
    private static final long serialVersionUID = 1L;

    protected transient BooleanItemVsItem expression;

    public CountableItem() {
    }

    public CountableItem(String checkedId, CompareOperator compareOperator, Integer argument) {
        super(checkedId, compareOperator, argument);
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

    public Integer getItemAmount(BooleanItemExpression<Asset> expression) {
        Asset checkingObject = expression.getCheckingObject();
        return checkingObject.getAmountById(checkedId);
    }

    public void setExpression(BooleanItemVsItem expression) {
        this.expression = expression;
    }
}
