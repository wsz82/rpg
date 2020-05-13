package view.stage;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

class EditorToolBar extends ToolBar {
    ToggleButton pointerButton = new ToggleButton();

    EditorToolBar() {
        pointerButton.setGraphic(new ImageView(getIcon()));
        this.getItems().addAll(pointerButton);
    }

    private Image getIcon() {
        return new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icons/cross.png"))
        );
    }
}
