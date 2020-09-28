package io.wsz.model.script.variable;

public class VariableBoolean extends Variable<Boolean>{
    private static final long serialVersionUID = 1L;

    public VariableBoolean() {
    }

    public VariableBoolean(Boolean value) {
        super(value);
    }

    public VariableBoolean(String id, Boolean value) {
        super(id, value);
    }

    @Override
    public VariableType getType() {
        return VariableType.BOOLEAN;
    }

    @Override
    public void setValue(String newVal) {
        value = getBooleanValue(newVal);
    }

    private Boolean getBooleanValue(String newValue) {
        return Boolean.parseBoolean(newValue);
    }
}
