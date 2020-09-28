package io.wsz.model.script.variable;

public class VariableString extends Variable<String>{
    private static final long serialVersionUID = 1L;

    public VariableString() {
    }

    public VariableString(String id, String value) {
        super(id, value);
    }

    @Override
    public VariableType getType() {
        return VariableType.STRING;
    }

    public VariableString(String value) {
        super(value);
    }

    @Override
    public void setValue(String newVal) {
        value = newVal;
    }
}
