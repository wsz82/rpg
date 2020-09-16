package io.wsz.model.script.bool;

import io.wsz.model.script.variable.Variable;

public abstract class BooleanVariableExpression<V extends Variable<?>> extends BooleanExpression<V> {
    private static final long serialVersionUID = 1L;

    protected V checkedVariable;

    public BooleanVariableExpression() {
    }

    public BooleanVariableExpression(String checkedID) {
        super(checkedID);
    }

    public V getCheckedVariable() {
        return checkedVariable;
    }
}
