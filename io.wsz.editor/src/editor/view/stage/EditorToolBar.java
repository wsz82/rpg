package editor.view.stage;

import editor.model.ActiveContent;
import editor.model.EditorController;
import io.wsz.model.layer.CurrentLayer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
        VBox opacityBox = createOpacityTool();

        getItems().addAll(pointerButton, opacityBox);
    }

    private VBox createOpacityTool() {
        final boolean[] isItem = new boolean[]{true};
        VBox opacityBox = new VBox(5);
        Label opacityLabel = new Label("Item opacity");
        opacityBox.getChildren().addAll(opacityLabel, opacitySlider);

        opacityLabel.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (isItem[0]) {
                    isItem[0] = false;
                    opacityLabel.setText("Layer opacity");
                } else {
                    isItem[0] = true;
                    opacityLabel.setText("Item opacity");
                }
            }
        });

        opacitySlider.setShowTickMarks(true);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setMajorTickUnit(0.3);
        opacitySlider.setBlockIncrement(0.1);

        EditorController.get().getActiveContent().imageProperty().addListener((observable, oldValue, newValue) -> {
            opacitySlider.setValue(newValue.getOpacity());
        });

        opacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int currentLevel = CurrentLayer.get().getLevel();
            if (isItem[0]) {
                ActiveContent ac = EditorController.get().getActiveContent();
                ImageView iv = ac.getImage();
                if (iv == null) {
                    return;
                }
                iv.setOpacity(newValue.doubleValue());
            } else {
                EditorBoard.get().getBoardContents().stream()
                        .filter(c -> c.getContent().getItem().getLevel() == currentLevel)
                        .forEach(i -> i.getImageView().setOpacity(newValue.doubleValue()));
            }
        });
        return opacityBox;
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
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png"))
        );
    }
}
