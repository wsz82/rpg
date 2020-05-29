package game.view.stage;

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;
import model.Controller;
import model.content.Content;
import model.location.CurrentLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GameScrollPane extends ScrollPane {
    private static final double SCROLL_H_FACTOR = 0.05;
    private static final double SCROLL_V_FACTOR = 0.05;
    private static GameScrollPane singleton;
    private int[] levels;

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
        hookupEvents();
    }

    private void hookupEvents() {
        CurrentLocation.get().locationProperty().addListener(observable -> {
            levels = getLevelsArr();
        });

        addEventFilter(ScrollEvent.SCROLL, Event::consume);
        addEventFilter(MouseEvent.ANY, event -> {
            if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    && !event.getEventType().equals(MouseEvent.MOUSE_CLICKED)
                    && event.getButton() != MouseButton.MIDDLE) event.consume();
        });

        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode key = e.getCode();
            switch (key) {
                case UP, DOWN, LEFT, RIGHT -> e.consume();
                case PAGE_UP -> {
                    e.consume();
                    int level = Controller.get().getCurrentLayer().getLevel();
                    int next = level;
                    for (int i = 0; i < levels.length - 1; i++) {
                        int current = levels[i];
                        if (current == level) {
                            next = levels[i + 1];
                        }
                    }
                    Controller.get().getCurrentLayer().setLevel(next);
                }
                case PAGE_DOWN -> {
                    e.consume();
                    int level = Controller.get().getCurrentLayer().getLevel();
                    int prev = level;
                    for (int i = 1; i < levels.length; i++) {
                        int current = levels[i];
                        if (current == level) {
                            prev = levels[i - 1];
                        }
                    }
                    Controller.get().getCurrentLayer().setLevel(prev);
                }
            }
        });
    }

    private int[] getLevelsArr() {
        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        return contents.stream()
                .mapToInt(l -> l.getItem().getLevel())
                .distinct()
                .sorted()
                .toArray();
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
