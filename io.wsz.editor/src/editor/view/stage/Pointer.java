package editor.view.stage;

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

                mark = new Coords(event.getX(), event.getY());
                if (markerImage == null) {
                    loadMarkerImage();
                }
                double x = mark.getX() - markerImage.getWidth()/2;
                double y = mark.getY() - markerImage.getHeight()/2;
                EditorCanvas.get().drawMarker(markerImage, x, y);
            }
        }
    };
    private static Coords mark;
    private static Pointer singleton;
    private static boolean active;
    private Image markerImage;

    static Pointer get() {
        if (singleton == null) {
            singleton = new Pointer();
        }
        return singleton;
}

    private Pointer() {
    }

    private void loadMarkerImage() {
        markerImage = new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pointer.png"))
        );
    }

    void activate() {
        active = true;
        EditorCanvas.get().setCursor(Cursor.CROSSHAIR);
        EditorCanvas.get().addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    void deactivate() {
        active = false;
        EditorCanvas.get().setCursor(Cursor.DEFAULT);
        EditorCanvas.get().removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        EditorCanvas.get().removeMarker();
    }

    public static Coords getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coords(0, 0);
        }
    }
}
