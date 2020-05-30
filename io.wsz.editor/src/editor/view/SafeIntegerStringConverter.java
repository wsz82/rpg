package editor.view;

import javafx.util.converter.IntegerStringConverter;

public class SafeIntegerStringConverter extends IntegerStringConverter {
    @Override
    public Integer fromString(String string) {
        Integer result;

        try {
            result = super.fromString(string);
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }
}
