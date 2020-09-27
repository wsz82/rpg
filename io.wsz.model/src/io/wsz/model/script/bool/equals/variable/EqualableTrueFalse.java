package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.equals.Equalable;

public class EqualableTrueFalse extends Equalable<Boolean> {
    private static final long serialVersionUID = 1L;

    private transient BooleanTrueFalseGlobalVariable expression;

    public EqualableTrueFalse() {
    }

    public EqualableTrueFalse(String checkedId, EqualsOperator equalsOperator, Boolean argument) {
        super(checkedId, equalsOperator, argument);
    }

    @Override
    protected boolean isEqual(Boolean value) {
        return value.equals(argument);
    }

    @Override
    protected boolean isNotEqual(Boolean value) {
        return !value.equals(argument);
    }

    @Override
    public Boolean getValue() {
        return expression.getCheckingObject().getValue();
    }

    public void setExpression(BooleanTrueFalseGlobalVariable expression) {
        this.expression = expression;
    }
}
