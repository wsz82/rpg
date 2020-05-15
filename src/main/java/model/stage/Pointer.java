package model.stage;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import view.stage.MainView;

import java.util.Objects;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                mark = new Coordinates(event);
                boolean markerIsNotDrawn = !MainView.getBoard().getChildren().contains(marker);
                if (markerIsNotDrawn) {
                    loadMarkerImage();
                    marker.setImage(markerImage);
                    MainView.getBoard().getChildren().add(marker);
                }
                MainView.getBoard().setLeftAnchor(marker, mark.getX() - markerImage.getWidth()/2);
                MainView.getBoard().setTopAnchor(marker, mark.getY() - markerImage.getHeight()/2);
            }
        }
    };
    private final ImageView marker = new ImageView();
    private static Coordinates mark;
    private static Pointer pointer;
    private static boolean active;
    private Image markerImage;

    public static Pointer getInstance() {
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

    public void activate() {
        active = true;
        MainView.getBoard().setCursor(Cursor.CROSSHAIR);
        MainView.getBoard().setOnMouseClicked(clickEvent);
    }

    public void deactivate() {
        active = false;
        MainView.getBoard().setCursor(Cursor.DEFAULT);
        MainView.getBoard().removeEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        MainView.getBoard().getChildren().remove(marker);
    }

    public static Coordinates getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coordinates(0, 0);
        }
    }
}
