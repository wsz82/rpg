package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;

public abstract class BooleanNumberGlobalVariable<N extends Number> extends BooleanVariableExpression<Variable<N>> {

    public BooleanNumberGlobalVariable() {
    }

    public BooleanNumberGlobalVariable(String checkingId) {
        super(checkingId);
    }
}
