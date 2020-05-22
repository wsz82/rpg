package editor.view.stage;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import model.stage.Coords;

class CoordinatesBox extends HBox {
    private final EventHandler<MouseEvent> moveEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
                int z = EditorBoard.get().getzPos();
                Coords coords = new Coords(event, z);
                mouseX.setText("X: " + coords.getX());
                mouseY.setText("Y: " + coords.getY());
                mouseZ.setText("Z: " + coords.getZ());
            }
        }
    };
    private final Label mouseX = new Label();
    private final Label mouseY = new Label();
    private final Label mouseZ = new Label();
    private final Region root;

    CoordinatesBox(Region region) {
        this.root = region;
        create();
    }

    private void create() {
        root.addEventHandler(MouseEvent.MOUSE_MOVED, moveEvent);
        getChildren().addAll(mouseX, mouseY, mouseZ);
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(5);
    }
}
