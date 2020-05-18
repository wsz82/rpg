package view.stage;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.stage.Coordinates;

import java.util.Objects;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                int z = Board.get().getzPos();
                boolean markerIsNotDrawn = !Board.get().getChildren().contains(marker);

                mark = new Coordinates(event, z);
                if (markerIsNotDrawn) {
                    loadMarkerImage();
                    marker.setImage(markerImage);
                    Board.get().getChildren().add(marker);
                }
                Board.get().setLeftAnchor(marker, mark.getX() - markerImage.getWidth()/2);
                Board.get().setTopAnchor(marker, mark.getY() - markerImage.getHeight()/2);
            }
        }
    };
    private final ImageView marker = new ImageView();
    private static Coordinates mark;
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
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("icons/pointer.png"))
        );
    }

    void activate() {
        active = true;
        Board.get().setCursor(Cursor.CROSSHAIR);
        Board.get().addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    void deactivate() {
        active = false;
        Board.get().setCursor(Cursor.DEFAULT);
        Board.get().removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        Board.get().getChildren().remove(marker);
    }

    public static Coordinates getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coordinates(0, 0, 0);
        }
    }
}
