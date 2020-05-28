package game.view.stage;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.awt.*;

public class GameScrollPane extends ScrollPane {
    private static final double SCROLL_H_FACTOR = 10;
    private static final double SCROLL_V_FACTOR = 3;
    private static GameScrollPane singleton;

    public static GameScrollPane get() {
        if (singleton == null) {
            singleton = new GameScrollPane(GameBoard.get());
        }
        return singleton;
    }

    private GameScrollPane(Node node){
        super(node);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        setPannable(true);
        addEventFilter(ScrollEvent.SCROLL, Event::consume);
        addEventFilter(MouseEvent.ANY, event -> {
            if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    && !event.getEventType().equals(MouseEvent.MOUSE_CLICKED)
                    && event.getButton() != MouseButton.MIDDLE) event.consume();
        });

        setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.RIGHT
                    || e.getCode() == KeyCode.LEFT) {
                e.consume();
            }
        });
    }

    public void updatePos() {
        if (!GameMenu.get().isFullScreen()) {
            return;
        }
        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;
        int width = (int) getWidth();
        int height = (int) getHeight();
        double unnormalizedScrollHFactor = SCROLL_H_FACTOR / getWidth();
        double unnormalizedScrollVFactor = SCROLL_V_FACTOR / getHeight();

        if (x < 10 && x >= 0) {
            setHvalue(getHvalue() - unnormalizedScrollHFactor);
        } else
        if (x > width - 10 && x <= width) {
            setHvalue(getHvalue() + unnormalizedScrollHFactor);
        } else
        if (y < 10 && y >= 0) {
            setVvalue(getVvalue() - unnormalizedScrollVFactor);
        } else
        if (y > height - 10 && y <= height) {
            setVvalue(getVvalue() + unnormalizedScrollVFactor);
        }
    }
}
