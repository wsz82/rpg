package editor.view.utilities;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

public abstract class NumberField<N extends Number> extends TextField {

    public NumberField(boolean canBeEmpty) {
        setUpFilter(null, canBeEmpty);
    }

    public NumberField(N min, boolean canBeEmpty) {
        setUpFilter(min, canBeEmpty);
    }

    private void setUpFilter(N min, boolean canBeEmpty) {
        ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
            N newNumber = getDefaultValue();

            try {
                newNumber = getNumber(newValue);
            } catch (NumberFormatException|NullPointerException e) {
                if (canBeEmpty && (newValue == null || newValue.isEmpty()) || newValue.equals("null")) {
                    setText("");
                    return;
                }
                if (oldValue.isEmpty()) {
                    oldValue = String.valueOf(newNumber);
                }
                setText("" + oldValue);
            }
            resetIfBelowMin(min, oldValue, newNumber);
        };
        textProperty().addListener(positiveNumberListener);
    }

    protected abstract N getDefaultValue();

    private void resetIfBelowMin(N min, String oldValue, N newNumber) {
        if (min == null) {
            return;
        }
        if (isBelowMin(min, newNumber)) setText("" + oldValue);
    }

    protected abstract boolean isBelowMin(N min, N newNumber);

    public N getValue() {
        try {
            String text = getText();
            return getNumber(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected abstract N getNumber(String text);
}
