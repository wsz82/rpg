package io.wsz.model.script.variable;

public class VariableDecimal extends VariableNumber<Double>{
    private static final long serialVersionUID = 1L;

    public VariableDecimal() {
    }

    public VariableDecimal(Double value) {
        super(value);
    }

    public VariableDecimal(String id, Double value) {
        super(id, value);
    }

    @Override
    public VariableType getType() {
        return VariableType.DECIMAL;
    }

    @Override
    public void setValue(String newVal) {
        value = getDoubleValue(newVal);
    }

    private Double getDoubleValue(String newValue) {
        try {
            return Double.parseDouble(newValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
