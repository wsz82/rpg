package io.wsz.model.script.bool.countable;

import io.wsz.model.script.CompareOperator;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Countable<N extends Number> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected CompareOperator compareOperator;
    protected N argument;

    public Countable() {
    }

    public Countable(CompareOperator compareOperator, N argument) {
        this.compareOperator = compareOperator;
        this.argument = argument;
    }

    public boolean isFitAmount() {
        return switch (compareOperator) {
            case EQUAL -> isEqual();
            case NOT_EQUAL -> isNotEqual();
            case GREATER -> isGreater();
            case GREATER_OR_EQUAL -> isGreaterOrEqual();
            case LESSER -> isLesser();
            case LESSER_OR_EQUAL -> isLesserOrEqual();
        };
    }

    public boolean isLesserOrEqual() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return isLesserOrEqual(amount);
    }

    protected abstract boolean isLesserOrEqual(N amount);

    public boolean isLesser() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return isLesser(amount);
    }

    protected abstract boolean isLesser(N amount);

    public boolean isGreaterOrEqual() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return isGreaterOrEqual(amount);
    }

    protected abstract boolean isGreaterOrEqual(N amount);

    public boolean isGreater() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return isGreater(amount);
    }

    protected abstract boolean isGreater(N amount);

    public boolean isNotEqual() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return isNotEqual(amount);
    }

    protected abstract boolean isNotEqual(N amount);

    public boolean isEqual() {
        N amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return isEqual(amount);
    }

    protected abstract boolean isEqual(N amount);

    public abstract N getAmount();

    public CompareOperator getCompareOperator() {
        return compareOperator;
    }

    public void setCompareOperator(CompareOperator compareOperator) {
        this.compareOperator = compareOperator;
    }

    public N getArgument() {
        return argument;
    }

    public void setArgument(N argument) {
        this.argument = argument;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(compareOperator);
        out.writeObject(argument);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        compareOperator = (CompareOperator) in.readObject();
        argument = (N) in.readObject();
    }
}
