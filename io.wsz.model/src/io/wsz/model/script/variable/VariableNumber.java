package io.wsz.model.script.variable;

public abstract class VariableNumber<N extends Number> extends Variable<N> {

    public VariableNumber() {
    }

    public VariableNumber(N value) {
        super(value);
    }

    public VariableNumber(String id, N value) {
        super(id, value);
    }
}
