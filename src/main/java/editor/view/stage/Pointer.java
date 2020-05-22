package editor.view.stage;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.stage.Coords;

import java.util.Objects;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            event.consume();
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int z = EditorBoard.get().getzPos();
                boolean markerIsNotDrawn = !EditorBoard.get().getChildren().contains(marker);

                mark = new Coords(event, z);
                if (markerIsNotDrawn) {
                    loadMarkerImage();
                    marker.setImage(markerImage);
                    EditorBoard.get().getChildren().add(marker);
                }
                EditorBoard.get().setLeftAnchor(marker, mark.getX() - markerImage.getWidth()/2);
                EditorBoard.get().setTopAnchor(marker, mark.getY() - markerImage.getHeight()/2);
            }
        }
    };
    private final ImageView marker = new ImageView();
    private static Coords mark;
    private static Pointer pointer;
    private static boolean active;
    private Image markerImage;

    static Pointer getInstance() {
        if (pointer == null) {
            pointer = new Pointer();
        }
        return pointer;
    }

    private Pointer() {
    }

    private void loadMarkerImage() {
        markerImage = new Image(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("editor/icons/pointer.png"))
        );
    }

    void activate() {
        active = true;
        EditorBoard.get().setCursor(Cursor.CROSSHAIR);
        EditorBoard.get().addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    void deactivate() {
        active = false;
        EditorBoard.get().setCursor(Cursor.DEFAULT);
        EditorBoard.get().removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        EditorBoard.get().getChildren().remove(marker);
    }

    public static Coords getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coords(0, 0, 0);
        }
    }
}
