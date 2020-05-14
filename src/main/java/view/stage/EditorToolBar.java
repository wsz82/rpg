package view.stage;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.stage.Pointer;

import java.util.Objects;

class EditorToolBar extends ToolBar {
    private final ToggleButton pointerButton = new ToggleButton();
    private final Stage parent;

    EditorToolBar(Stage parent) {
        this.parent = parent;

        pointerButton.setGraphic(new ImageView(getPointerIcon()));
        pointerButton.setOnAction(event -> {
            if (pointerButton.isSelected()) {
                Pointer.getInstance().activate();
            } else {
                Pointer.getInstance().deactivate();
            }
        });

        this.getItems().addAll(pointerButton);
    }

    private Image getPointerIcon() {
        return new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icons/pointer.png"))
        );
    }
}
