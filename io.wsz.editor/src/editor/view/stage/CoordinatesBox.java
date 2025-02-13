package editor.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

class CoordinatesBox extends HBox {
    private final Controller controller;
    private final Coords mousePos = new Coords();
    private final Label mouseX = new Label();
    private final Label mouseY = new Label();
    private final Pane center;

    public CoordinatesBox(Pane center, Controller controller) {
        this.controller = controller;
        this.center = center;
        hookUpEvents();
    }

    private void hookUpEvents() {
        EventHandler<MouseEvent> moveEvent = event -> {
            if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                mousePos.x = event.getX() / Sizes.getMeter();
                mousePos.y = event.getY() / Sizes.getMeter();
                Coords boardPos = controller.getCurPos();
                mousePos.add(boardPos);
                mouseX.setText("X: " + String.format("%.2f", mousePos.x));
                mouseY.setText("Y: " + String.format("%.2f", mousePos.y));
            }
        };
        center.addEventHandler(MouseEvent.MOUSE_MOVED, moveEvent);
    }

    public void initBox() {
        getChildren().addAll(mouseX, mouseY);
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(5);
    }
}
