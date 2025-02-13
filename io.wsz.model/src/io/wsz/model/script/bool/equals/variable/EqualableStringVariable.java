package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.equals.Equalable;

public class EqualableStringVariable extends Equalable<String> {
    private static final long serialVersionUID = 1L;

    private transient BooleanStringVariableEquals expression;

    public EqualableStringVariable() {
    }

    public EqualableStringVariable(String checkedId, EqualsOperator equalsOperator, String argument) {
        super(checkedId, equalsOperator, argument);
    }

    @Override
    protected boolean isEqual(String value) {
        return value.equals(argument);
    }

    @Override
    protected boolean isNotEqual(String value) {
        return !value.equals(argument);
    }

    @Override
    public String getValue() {
        return expression.getCheckingObject().getValue();
    }

    public void setExpression(BooleanStringVariableEquals expression) {
        this.expression = expression;
    }
}
