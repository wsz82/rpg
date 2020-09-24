package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.script.BooleanType;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanTrueFalseGlobalVariable extends BooleanVariableExpression<Variable<Boolean>> {
    private static final long serialVersionUID = 1L;

    private BooleanType booleanType;

    public BooleanTrueFalseGlobalVariable() {
    }

    public BooleanTrueFalseGlobalVariable(String checkedID) {
        super(checkedID);
    }

    @Override
    public boolean isTrue(Variable<Boolean> variable) {
        return switch (booleanType) {
            case TRUE -> variable.getValue();
            case FALSE -> !variable.getValue();
        };
    }

    public BooleanType getBooleanType() {
        return booleanType;
    }

    public void setBooleanType(BooleanType booleanType) {
        this.booleanType = booleanType;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(booleanType);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        booleanType = (BooleanType) in.readObject();
    }
}
