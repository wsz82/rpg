package editor.view.utilities;

import javafx.scene.control.ChoiceBox;
import javafx.util.StringConverter;

public abstract class ToStringConverter<O> extends StringConverter<O> {
    private final ChoiceBox<O> choiceBox;

    public ToStringConverter(ChoiceBox<O> choiceBox) {
        this.choiceBox = choiceBox;
    }

    @Override
    public O fromString(String string) {
        return choiceBox.getValue();
    }

}
