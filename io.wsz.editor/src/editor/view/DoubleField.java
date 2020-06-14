package editor.view;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DoubleStringConverter;

import java.util.function.UnaryOperator;

public class DoubleField extends TextField {

    public DoubleField() {
        filterTextFieldForDouble(null);
    }

    public DoubleField(Double min) {
        filterTextFieldForDouble(min);
    }

    private void filterTextFieldForDouble(Double min) {
        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String input = change.getControlNewText();
            try {
                Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return null;
            }
            return change;
        };
        if (min != null) {
            setTextFormatter(new TextFormatter<>(new DoubleStringConverter(), min, doubleFilter));
            ChangeListener<String> positiveNumberListener = (observable, oldValue, newValue) -> {
                double newNumber = min;

                try {
                    newNumber = Double.parseDouble(newValue);
                } catch (NumberFormatException e) {
                    setText("" + oldValue);
                }
                if (newNumber < min) setText("" + oldValue);
            };
            textProperty().addListener(positiveNumberListener);
        }
    }
}
