package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.Countable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CountableItem extends Countable<Integer> {
    private static final long serialVersionUID = 1L;

    protected String checkedId;

    protected transient BooleanItemVsItem expression;

    public CountableItem() {
    }

    public CountableItem(String checkedId, CompareOperator compareOperator, Integer argument) {
        super(compareOperator, argument);
        this.checkedId = checkedId;
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

    public String getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(String checkedId) {
        this.checkedId = checkedId;
    }

    public void setExpression(BooleanItemVsItem expression) {
        this.expression = expression;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(checkedId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        checkedId = (String) in.readObject();
    }
}
