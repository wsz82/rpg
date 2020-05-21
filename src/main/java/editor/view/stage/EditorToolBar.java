package editor.view.stage;

import editor.model.EditorController;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

class EditorToolBar extends ToolBar {
    private final ToggleButton pointerButton = new ToggleButton();
    private final Slider opacitySlider = new Slider(0.1, 1, 1);
    private final Stage parent;

    EditorToolBar(Stage parent) {
        this.parent = parent;

        createPointerTool();

        VBox opacityBox = new VBox(5);
        Label opacityLabel = new Label("Opacity");
        opacityBox.getChildren().addAll(opacityLabel, opacitySlider);
        createOpacityTool();

        getItems().addAll(pointerButton, opacityBox);
    }

    private void createOpacityTool() {
        opacitySlider.setShowTickMarks(true);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setMajorTickUnit(0.3);
        opacitySlider.setBlockIncrement(0.1);

        EditorController.get().activeImageProperty().addListener((observable, oldValue, newValue) -> {
            opacitySlider.setValue(newValue.getOpacity());
        });

        opacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ImageView iv = EditorController.get().getActiveImage();
            if (iv != null) {
                iv.setOpacity(newValue.doubleValue());
            }
        });
    }

    private void createPointerTool() {
        pointerButton.setGraphic(new ImageView(getPointerIcon()));
        pointerButton.setOnAction(event -> {
            if (pointerButton.isSelected()) {
                Pointer.getInstance().activate();
            } else {
                Pointer.getInstance().deactivate();
            }
        });
    }

    private Image getPointerIcon() {
        return new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("editor/icons/pointer.png"))
        );
    }
}
