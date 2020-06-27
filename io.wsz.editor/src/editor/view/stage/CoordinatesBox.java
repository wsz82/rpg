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
    private final EventHandler<MouseEvent> moveEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                Coords mousePos = new Coords(event.getX() / Sizes.getMeter(), event.getY() / Sizes.getMeter());
                Controller.get();
                Coords boardPos = Controller.get().getBoardPos();
                Coords translated = mousePos.add(boardPos);
                mouseX.setText("X: " + String.format("%.2f", translated.x));
                mouseY.setText("Y: " + String.format("%.2f", translated.y));
            }
        }
    };
    private final Label mouseX = new Label();
    private final Label mouseY = new Label();
    private final Pane center;

    CoordinatesBox(Pane center) {
        this.center = center;
        init();
    }

    private void init() {
        center.addEventHandler(MouseEvent.MOUSE_MOVED, moveEvent);
        getChildren().addAll(mouseX, mouseY);
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(5);
    }
}
