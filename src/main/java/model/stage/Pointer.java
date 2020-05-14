package model.stage;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import view.stage.MainView;

public class Pointer {
    private final EventHandler<MouseEvent> clickEvent = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                mark = new Coordinates(MainView.getBoard(), event);
            }
        }
    };
    private static Coordinates mark;
    private static Pointer pointer;
    private static boolean active;

    public static Pointer getInstance() {
        if (pointer == null) {
            pointer = new Pointer();
        }
        return pointer;
    }

    private Pointer() {
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
    }

    public static Coordinates getMark() {
        if (mark != null && active) {
            return mark;
        } else {
            return new Coordinates(0, 0);
        }
    }
}
