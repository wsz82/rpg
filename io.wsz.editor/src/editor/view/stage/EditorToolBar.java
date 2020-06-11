package editor.view.stage;

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

    EditorToolBar(Pointer pointer) {
        createPointerTool(pointer);
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
        opacitySlider.pressedProperty().addListener((observable, oldValue, newValue) -> {
           //TODO changing opacity
        });
    }

    private void createPointerTool(Pointer pointer) {
        pointerButton.setGraphic(new ImageView(getPointerIcon()));
        pointerButton.setOnAction(event -> {
            if (pointerButton.isSelected()) {
                pointer.activate();
            } else {
                pointer.deactivate();
            }
        });
    }

    private Image getPointerIcon() {
        return new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png"))
        );
    }
}
