package editor.view;

public class DoubleField extends NumberField<Double> {

    public DoubleField(boolean canBeEmpty) {
        super(canBeEmpty);
    }

    public DoubleField(Double min, boolean canBeEmpty) {
        super(min, canBeEmpty);
    }

    @Override
    protected Double getDefaultValue() {
        return 0.0;
    }

    @Override
    protected boolean isBelowMin(Double min, Double newNumber) {
        return newNumber < min;
    }

    @Override
    protected Double getNumber(String text) {
        return Double.parseDouble(text);
    }
}
