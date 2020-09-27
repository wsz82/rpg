package io.wsz.model.script.bool.equals;

import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.Comparable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Equalable<O> extends Comparable {
    private static final long serialVersionUID = 1L;

    protected EqualsOperator equalsOperator;
    protected O argument;

    public Equalable() {
    }

    public Equalable(String checkedId, EqualsOperator equalsOperator, O argument) {
        super(checkedId);
        this.equalsOperator = equalsOperator;
        this.argument = argument;
    }

    public boolean isFitAmount() {
        return switch (equalsOperator) {
            case EQUAL -> isEqual();
            case NOT_EQUAL -> isNotEqual();
        };
    }

    public boolean isEqual() {
        O amount;
        try {
            amount = getValue();
        } catch (NullPointerException e) {
            return false;
        }
        return isEqual(amount);
    }

    protected abstract boolean isEqual(O amount);

    public boolean isNotEqual() {
        O amount;
        try {
            amount = getValue();
        } catch (NullPointerException e) {
            return true;
        }
        return isNotEqual(amount);
    }

    protected abstract boolean isNotEqual(O amount);

    public abstract O getValue();

    public EqualsOperator getEqualsOperator() {
        return equalsOperator;
    }

    public void setEqualsOperator(EqualsOperator equalsOperator) {
        this.equalsOperator = equalsOperator;
    }

    public O getArgument() {
        return argument;
    }

    public void setArgument(O argument) {
        this.argument = argument;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(equalsOperator);
        out.writeObject(argument);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        equalsOperator = (EqualsOperator) in.readObject();
        argument = (O) in.readObject();
    }
}
