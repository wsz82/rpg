package editor.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

public class DoubleField extends TextField {

    public DoubleField(boolean canBeEmpty) {
        filterTextFieldForDouble(null, canBeEmpty);
    }

    public DoubleField(Double min, boolean canBeEmpty) {
        filterTextFieldForDouble(min, canBeEmpty);
    }

    private void filterTextFieldForDouble(Double min, boolean canBeEmpty) {
        ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
            double newNumber = 0.0;

            try {
                newNumber = Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                if (canBeEmpty && newValue.isEmpty()) {
                    setText("");
                    return;
                }
                if (oldValue.isEmpty()) {
                    oldValue = "0.0";
                }
                setText("" + oldValue);
            }
            resetIfBelowMin(min, oldValue, newNumber);
        };
        textProperty().addListener(positiveNumberListener);
    }

    private void resetIfBelowMin(Double min, String oldValue, double newNumber) {
        if (min == null) {
            return;
        }
        if (newNumber < min) setText("" + oldValue);
    }
}
