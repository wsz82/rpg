package io.wsz.model.script;

import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class BooleanCountableExpression<A extends PosItem<?,?>> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String itemID;
    protected CompareOperator compareOperator;
    protected int argument;
    protected A checkedItem;

    public BooleanCountableExpression() {}

    public BooleanCountableExpression(CompareOperator compareOperator, String itemID, int argument) {
        this.compareOperator = compareOperator;
        this.itemID = itemID;
        this.argument = argument;
    }

    public abstract boolean isTrue(A checkedItem);

    protected boolean isLesserOrEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount <= argument;
    }

    protected boolean isLesser() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount < argument;
    }

    protected boolean isGreaterOrEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount >= argument;
    }

    protected boolean isGreater() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount > argument;
    }

    protected boolean isNotEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount != argument;
    }

    protected boolean isEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount == argument;
    }

    protected abstract long getAmount();

    public CompareOperator getCompareOperator() {
        return compareOperator;
    }

    public void setCompareOperator(CompareOperator compareOperator) {
        this.compareOperator = compareOperator;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getArgument() {
        return argument;
    }

    public void setArgument(int argument) {
        this.argument = argument;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(compareOperator);
        out.writeObject(itemID);
        out.writeInt(argument);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        compareOperator = (CompareOperator) in.readObject();
        itemID = (String) in.readObject();
        argument = in.readInt();
    }
}
