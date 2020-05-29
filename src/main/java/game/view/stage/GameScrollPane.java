package game.view.stage;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import model.Controller;

import java.awt.*;

public class GameScrollPane extends ScrollPane {
    private static final double SCROLL_H_FACTOR = 0.05;
    private static final double SCROLL_V_FACTOR = 0.05;
    private static GameScrollPane singleton;

    public static GameScrollPane get() {
        if (singleton == null) {
            singleton = new GameScrollPane(GameCanvas.get());
        }
        return singleton;
    }

    private GameScrollPane(Node node){
        super(node);

        setSize();

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

    private void setSize() {
        double viewportWidth = GameStage.get().getWidth();
        double viewportHeight = GameStage.get().getHeight();
        setPrefSize(viewportWidth, viewportHeight);

        GameStage.get().widthProperty().addListener(observable -> {
            double width = GameStage.get().getWidth();
            setPrefWidth(width);
        });
        GameStage.get().heightProperty().addListener(observable -> {
            double height = GameStage.get().getHeight();
            setPrefHeight(height);
        });
    }

    public void updatePos() {
        if (!GameStage.get().isFullScreen()) {
            return;
        }
        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;
        double width = (int) getWidth();
        double canvasWidth = Controller.get().getCurrentLocation().getCurrentWidth();
        double height = (int) getHeight();
        double canvasHeight = Controller.get().getCurrentLocation().getCurrentHeight();
        double normalizedScrollHFactor = SCROLL_H_FACTOR * getWidth() / canvasWidth;
        double normalizedScrollVFactor = SCROLL_V_FACTOR * getHeight() / canvasHeight;

        if (x < 10 && x >= 0) {
            setHvalue(getHvalue() - normalizedScrollHFactor);
        } else
        if (x > width - 10 && x <= width) {
            setHvalue(getHvalue() + normalizedScrollHFactor);
        } else
        if (y < 10 && y >= 0) {
            setVvalue(getVvalue() - normalizedScrollVFactor);
        } else
        if (y > height - 10 && y <= height) {
            setVvalue(getVvalue() + normalizedScrollVFactor);
        }
    }
}
