package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.bool.countable.Countable;
import io.wsz.model.script.variable.Variable;

public abstract class CountableVariable<N extends Number> extends Countable<N>{
    private static final long serialVersionUID = 1L;

    protected transient BooleanVariableExpression<Variable<N>> expression;

    public CountableVariable() {
    }

    public CountableVariable(CompareOperator compareOperator, N argument) {
        super(compareOperator, argument);
    }

    public void setExpression(BooleanVariableExpression<Variable<N>> expression) {
        this.expression = expression;
    }
}
