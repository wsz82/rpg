package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.script.variable.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanIntegerGlobalVariable extends BooleanNumberGlobalVariable<Integer> {
    private static final long serialVersionUID = 1L;

    private CountableIntegerVariable countable;

    public BooleanIntegerGlobalVariable() {
    }

    public BooleanIntegerGlobalVariable(String checkedID, CountableIntegerVariable countable) {
        super(checkedID);
        this.countable = countable;
        this.countable.setExpression(this);
    }

    @Override
    public boolean isTrue(Variable<Integer> checkedVariable) {
        this.checkedVariable = checkedVariable;
        return countable.isFitAmount();
    }

    public CountableIntegerVariable getCountable() {
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
        countable = (CountableIntegerVariable) in.readObject();
        countable.setExpression(this);
    }
}
