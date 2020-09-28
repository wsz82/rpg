package io.wsz.model.script.variable;

public class VariableInteger extends Variable<Integer>{
    private static final long serialVersionUID = 1L;

    public VariableInteger() {
    }

    public VariableInteger(Integer value) {
        super(value);
    }

    public VariableInteger(String id, Integer value) {
        super(id, value);
    }

    @Override
    public VariableType getType() {
        return VariableType.INTEGER;
    }

    @Override
    public void setValue(String newVal) {
        value = getIntegerValue(newVal);
    }

    private Integer getIntegerValue(String newValue) {
        try {
            return Integer.parseInt(newValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
