package editor.view.stage;

import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

class CoordinatesBox extends HBox {
    private final EventHandler<MouseEvent> moveEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                Coords coords = new Coords(event.getX(), event.getY());
                mouseX.setText("X: " + coords.getX());
                mouseY.setText("Y: " + coords.getY());
            }
        }
    };
    private final Label mouseX = new Label();
    private final Label mouseY = new Label();
    private final Canvas canvas;

    CoordinatesBox(Canvas canvas) {
        this.canvas = canvas;
        create();
    }

    private void create() {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, moveEvent);
        getChildren().addAll(mouseX, mouseY);
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(5);
    }
}
