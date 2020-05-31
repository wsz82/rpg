package editor.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;

public class IntegerField extends TextField {

    public IntegerField() {
        filterTextFieldForInteger(null);
    }

    public IntegerField(Integer min) {
        filterTextFieldForInteger(min);
    }

    private void filterTextFieldForInteger(Integer min) {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getControlNewText();
            try {
                Integer.parseInt(input);
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        };
        if (min != null) {
            setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), min, integerFilter));
            ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
                int newNumber = min;

                try {
                    newNumber = Integer.parseInt(newValue);
                } catch (NumberFormatException e) {
                    setText("" + oldValue);
                }
                if (newNumber < min) setText("" + oldValue);
            };
            textProperty().addListener(positiveNumberListener);
        }
    }
}
