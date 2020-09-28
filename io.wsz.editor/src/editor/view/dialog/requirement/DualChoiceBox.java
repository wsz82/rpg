package editor.view.dialog.requirement;

import javafx.scene.control.ChoiceBox;

public class DualChoiceBox {
    private final ChoiceBox<?> first;
    private final ChoiceBox<?> second;

    public DualChoiceBox(ChoiceBox<?> first, ChoiceBox<?> second) {
        this.first = first;
        this.second = second;
    }

    public void hookUpEvents() {
        first.setOnAction(e -> {
            Object value = first.getValue();
            second.setVisible(value == null);
            checkDualInvisibility();
        });
        second.setOnAction(e -> {
            Object value = second.getValue();
            first.setVisible(value == null);
            checkDualInvisibility();
        });
    }

    private void checkDualInvisibility() {
        boolean bothAreInvisible = !first.isVisible() && !second.isVisible();
        if (bothAreInvisible) {
            first.setVisible(true);
        }
    }
}
