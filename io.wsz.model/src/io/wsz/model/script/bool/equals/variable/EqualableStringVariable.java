package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.script.EqualsOperator;
import io.wsz.model.script.bool.equals.Equalable;

public class EqualableStringVariable extends Equalable<String> {
    private static final long serialVersionUID = 1L;

    private transient BooleanStringVariableEquals expression;

    public EqualableStringVariable() {
    }

    public EqualableStringVariable(EqualsOperator equalsOperator, String argument) {
        super(equalsOperator, argument);
    }

    @Override
    protected boolean isEqual(String amount) {
        return amount.equals(argument);
    }

    @Override
    protected boolean isNotEqual(String amount) {
        return !amount.equals(argument);
    }

    @Override
    public String getValue() {
        return expression.getCheckingObject().getValue();
    }

    public void setExpression(BooleanStringVariableEquals expression) {
        this.expression = expression;
    }
}
