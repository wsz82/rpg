package editor.view.utilities;

import javafx.util.converter.IntegerStringConverter;

public class SafeIntegerStringConverter extends IntegerStringConverter {
    @Override
    public Integer fromString(String s) {
        Integer result;

        try {
            result = super.fromString(s);
        } catch (NumberFormatException e) {
            result = 0;
        }
        return result;
    }
}
