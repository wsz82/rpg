package editor.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

public class IntegerField extends TextField {

    public IntegerField(boolean canBeEmpty) {
        filterTextFieldForInteger(null, canBeEmpty);
    }

    public IntegerField(Integer min, boolean canBeEmpty) {
        filterTextFieldForInteger(min, canBeEmpty);
    }

    private void filterTextFieldForInteger(Integer min, boolean canBeEmpty) {
        ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
            int newNumber = 0;

            try {
                newNumber = Integer.parseInt(newValue);
            } catch (NumberFormatException|NullPointerException e) {
                if (canBeEmpty && (newValue == null || newValue.isEmpty())) {
                    setText("");
                    return;
                }
                if (oldValue.isEmpty()) {
                    oldValue = "0";
                }
                setText("" + oldValue);
            }
            resetIfBelowMin(min, oldValue, newNumber);
        };
        textProperty().addListener(positiveNumberListener);
    }

    private void resetIfBelowMin(Integer min, String oldValue, int newNumber) {
        if (min == null) {
            return;
        }
        if (newNumber < min) setText("" + oldValue);
    }
}
