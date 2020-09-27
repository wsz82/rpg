package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.script.CompareOperator;

public class CountableIntegerVariable extends CountableVariable<Integer>{
    private static final long serialVersionUID = 1L;

    public CountableIntegerVariable() {
    }

    public CountableIntegerVariable(CompareOperator compareOperator, Integer argument) {
        super(compareOperator, argument);
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
        return expression.getCheckingObject().getValue();
    }
}
