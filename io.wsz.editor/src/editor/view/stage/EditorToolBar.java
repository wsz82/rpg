package editor.view.stage;

import editor.model.ActiveContent;
import editor.model.EditorController;
import io.wsz.model.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

class EditorToolBar extends ToolBar {
    private final ToggleButton pointerButton = new ToggleButton();
    private final Slider opacitySlider = new Slider(0.1, 1, 1);

    EditorToolBar() {
        createPointerTool();
        VBox opacityBox = createOpacityTool();

        getItems().addAll(pointerButton, opacityBox);
    }

    private VBox createOpacityTool() {
        final String itemOpacity = "Item opacity";
        final String layerOpacity = "Layer opacity";
        final ObservableList<String> opacityList = FXCollections.observableArrayList();
        opacityList.add(itemOpacity);
        opacityList.add(layerOpacity);
        final ChoiceBox<String> cb = new ChoiceBox<>();
        cb.setItems(opacityList);
        final VBox opacityBox = new VBox(5);

        opacityBox.getChildren().addAll(cb, opacitySlider);

        cb.setValue(itemOpacity);
        opacitySlider.setShowTickMarks(true);
        opacitySlider.setShowTickLabels(true);
        opacitySlider.setMajorTickUnit(0.3);
        opacitySlider.setBlockIncrement(0.1);

        hookupOpacityEvents(itemOpacity, cb);

        return opacityBox;
    }

    private void hookupOpacityEvents(String itemOpacity, ChoiceBox<String> cb) {
        EditorController.get().getActiveContent().imageProperty().addListener((observable, oldValue, newValue) -> {
            opacitySlider.setValue(newValue.getOpacity());
        });

        opacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int currentLevel = Controller.get().getCurrentLayer().getLevel();
            if (cb.getValue().equals(itemOpacity)) {
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
    }

    private void createPointerTool() {
        pointerButton.setGraphic(new ImageView(getPointerIcon()));
        pointerButton.setOnAction(event -> {
            if (pointerButton.isSelected()) {
                Pointer.get().activate();
            } else {
                Pointer.get().deactivate();
            }
        });
    }

    private Image getPointerIcon() {
        return new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png"))
        );
    }
}
