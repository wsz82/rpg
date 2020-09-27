package io.wsz.model.script.bool.countable.variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanDecimalGlobalVariable extends BooleanNumberGlobalVariable<Double> {
    private static final long serialVersionUID = 1L;

    private CountableDecimalVariable countable;

    public BooleanDecimalGlobalVariable() {
    }

    public BooleanDecimalGlobalVariable(String checkedId, CountableDecimalVariable countable) {
        super(checkedId);
        this.countable = countable;
        this.countable.setExpression(this);
    }

    @Override
    public boolean isTrue() {
        return countable.isFitAmount();
    }

    @Override
    public CountableDecimalVariable getCountable() {
        return countable;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(countable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        countable = (CountableDecimalVariable) in.readObject();
        countable.setExpression(this);
    }
}
