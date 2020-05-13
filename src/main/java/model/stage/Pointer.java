package model.stage;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import view.stage.MainView;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                mark = new Coordinates(MainView.getCenter(), event);
            }
        }
    };
    private final Stage parent;
    private static Coordinates mark;
    private static Pointer pointer;
    private static boolean active;

    public static Pointer getInstance(Stage parent) {
        if (pointer == null) {
            pointer = new Pointer(parent);
        }
        return pointer;
    }

    private Pointer(Stage parent) {
        this.parent = parent;
    }

    public void activate() {
        active = true;
        MainView.getCenter().setCursor(Cursor.CROSSHAIR);
        MainView.getCenter().setOnMouseClicked(clickEvent);
    }

    public void deactivate() {
        active = false;
        MainView.getCenter().setCursor(Cursor.DEFAULT);
        MainView.getCenter().removeEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
    }

    public static Coordinates getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coordinates(0, 0);
        }
    }
}
