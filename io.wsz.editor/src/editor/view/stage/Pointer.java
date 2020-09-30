package editor.view.stage;

import editor.model.EditorController;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent;
    private final Coords mark = new Coords();

    private boolean active;
    private Image markerImage;
    private EditorCanvas editorCanvas;

    public Pointer(EditorController controller) {
        clickEvent = hookUpEvents(controller);
    }

    public EventHandler<MouseEvent> hookUpEvents(EditorController controller) {
        final EventHandler<MouseEvent> clickEvent;
        clickEvent = e -> {
            e.consume();
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                setMark(controller, e);
            }
        };
        return clickEvent;
    }

    private void setMark(EditorController controller, MouseEvent e) {
        mark.x = e.getX() / Sizes.getMeter();
        mark.y = e.getY() / Sizes.getMeter();
        mark.level = controller.getCurrentObservableLayer().getLevel();
        mark.add(controller.getCurPos());
        if (markerImage == null) {
            loadMarkerImage();
        }
        editorCanvas.refresh();
    }

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
            return new Coords();
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
