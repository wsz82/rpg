package game.view.stage;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;

import java.awt.*;

class GameScrollPane extends ScrollPane {
    private static final double SCROLL_H_FACTOR = 0.00003;
    private static final double SCROLL_V_FACTOR = 0.00001;

    GameScrollPane(Node node){
        super(node);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        scrollOnEdges();
        setPannable(true);
        addEventFilter(MouseEvent.ANY, e -> {
            if (!e.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    && e.getButton() != MouseButton.MIDDLE) e.consume();
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

    private void scrollOnEdges() {
        final Rectangle2D bounds = Screen.getPrimary().getBounds();
        final double width = bounds.getWidth();
        final double height = bounds.getHeight();

        addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            e.consume();
            Thread mousePosUpdater = new Thread(() -> {
                synchronized (this) {
                    double x = e.getScreenX();
                    while (x < 10 && x >= 0) {
                        setHvalue(getHvalue() - SCROLL_H_FACTOR);
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        x = p.x;
                    }
                    while (x > width - 10 && x <= width) {
                        setHvalue(getHvalue() + SCROLL_H_FACTOR);
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        x = p.x;
                    }
                    double y = e.getY();
                    while (y < 10 && y >= 0) {
                        setVvalue(getVvalue() - SCROLL_V_FACTOR);
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        y = p.y;
                    }
                    while (y > height - 10 && y <= height) {
                        setVvalue(getVvalue() + SCROLL_V_FACTOR);
                        Point p = MouseInfo.getPointerInfo().getLocation();
                        y = p.y;
                    }
                }
            });
            mousePosUpdater.start();
        });
    }
}
