package editor.view.dialog.requirement;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class DualTextFieldChoiceBox<T extends TextField> {
    private final T textField;
    private final ChoiceBox<?> choiceBox;

    public DualTextFieldChoiceBox(T textField, ChoiceBox<?> choiceBox) {
        this.textField = textField;
        this.choiceBox = choiceBox;
    }

    public void hookUpEvents() {
        textField.textProperty().addListener((observable, oldInput, newInput) -> {
            choiceBox.setVisible(newInput == null || newInput.isEmpty());
            checkDualInvisibility();
        });
        choiceBox.setOnAction(e -> {
            Object value = choiceBox.getValue();
            textField.setVisible(value == null);
            checkDualInvisibility();
        });
    }

    private void checkDualInvisibility() {
        boolean bothAreInvisible = !textField.isVisible() && !choiceBox.isVisible();
        if (bothAreInvisible) {
            textField.setVisible(true);
        }
    }
}
