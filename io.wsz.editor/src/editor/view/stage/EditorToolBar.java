package editor.view.stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Objects;

class EditorToolBar extends ToolBar {
    private static double itemsOpacity = 1;
    private static boolean layerOpacity;

    private final EditorCanvas editorCanvas;
    private final ToggleButton pointerButton = new ToggleButton();
    private final Slider opacitySlider = new Slider(0.1, 1, 1);

    EditorToolBar(EditorCanvas editorCanvas, Pointer pointer) {
        this.editorCanvas = editorCanvas;
        initToolBar(pointer);
    }

    private void initToolBar(Pointer pointer) {
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
        cb.setOnAction(e -> {
            String val = cb.getValue();
            layerOpacity = !val.equals(itemOpacity);
            editorCanvas.refresh();
        });

        EventHandler<MouseEvent> setOpacity = e -> {
            itemsOpacity = opacitySlider.getValue();
            editorCanvas.refresh();

        };
        opacitySlider.addEventHandler(MouseEvent.MOUSE_RELEASED, setOpacity);
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
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png")));
    }

    public static double getItemsOpacity() {
        return itemsOpacity;
    }

    public static boolean isLayerOpacity() {
        return layerOpacity;
    }
}
