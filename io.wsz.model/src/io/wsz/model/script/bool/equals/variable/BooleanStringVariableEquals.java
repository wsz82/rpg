package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanStringVariableEquals extends BooleanVariableExpression<Variable<String>> {
    private static final long serialVersionUID = 1L;

    private EqualableStringVariable equalable;

    public BooleanStringVariableEquals() {
    }

    public BooleanStringVariableEquals(String checkedID, EqualableStringVariable equalable) {
        super(checkedID);
        this.equalable = equalable;
        this.equalable.setExpression(this);
    }

    @Override
    public boolean isTrue(Variable<String> checkedVariable) {
        this.checkedVariable = checkedVariable;
        return equalable.isFitAmount();
    }

    public EqualableStringVariable getEqualable() {
        return equalable;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(equalable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        equalable = (EqualableStringVariable) in.readObject();
        equalable.setExpression(this);
    }
}
