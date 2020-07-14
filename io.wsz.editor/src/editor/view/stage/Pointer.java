package editor.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            event.consume();
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                mark.x = event.getX() / Sizes.getMeter();
                mark.y = event.getY() / Sizes.getMeter();
                mark.add(Controller.get().getBoardPos());
                if (markerImage == null) {
                    loadMarkerImage();
                }
                editorCanvas.refresh();
            }
        }
    };
    private final Coords mark = new Coords();
    private boolean active;
    private Image markerImage;
    private EditorCanvas editorCanvas;

    public Pointer() {}

    private void loadMarkerImage() {
        markerImage = new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png"))
        );
    }

    void activate() {
        active = true;
        editorCanvas.setCursor(Cursor.CROSSHAIR);
        editorCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    void deactivate() {
        active = false;
        editorCanvas.setCursor(Cursor.DEFAULT);
        editorCanvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        editorCanvas.refresh();
    }

    public Coords getMark() {
        if (mark.x != -1 && active) {
            return mark;
        } else {
            return new Coords(0, 0);
        }
    }

    public boolean isActive() {
        return active;
    }

    public Image getMarkerImage() {
        return markerImage;
    }

    public void setEditorCanvas(EditorCanvas editorCanvas) {
        this.editorCanvas = editorCanvas;
    }
}
