package editor.view;

public class IntegerField extends NumberField<Integer> {

    public IntegerField(boolean canBeEmpty) {
        super(canBeEmpty);
    }

    public IntegerField(Integer min, boolean canBeEmpty) {
        super(min, canBeEmpty);
    }

    @Override
    protected Integer getDefaultValue() {
        return 0;
    }

    @Override
    protected boolean isBelowMin(Integer min, Integer newNumber) {
        return newNumber < min;
    }

    @Override
    protected Integer getNumber(String text) {
        return Integer.parseInt(text);
    }
}
