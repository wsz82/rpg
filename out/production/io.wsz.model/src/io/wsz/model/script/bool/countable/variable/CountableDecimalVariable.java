package io.wsz.model.script.bool.countable.variable;

public class CountableDecimalVariable extends CountableVariable<Double>{
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean isLesserOrEqual(Double amount) {
        return amount <= argument;
    }

    @Override
    protected boolean isLesser(Double amount) {
        return amount < argument;
    }

    @Override
    protected boolean isGreaterOrEqual(Double amount) {
        return amount >= argument;
    }

    @Override
    protected boolean isGreater(Double amount) {
        return amount > argument;
    }

    @Override
    protected boolean isNotEqual(Double amount) {
        return !amount.equals(argument);
    }

    @Override
    protected boolean isEqual(Double amount) {
        return amount.equals(argument);
    }

    @Override
    public Double getAmount() {
        return expression.getCheckedVariable().getValue();
    }
}
