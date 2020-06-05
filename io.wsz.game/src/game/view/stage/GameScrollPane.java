package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameScrollPane extends ScrollPane {
    private static final double SCROLL_H_FACTOR = 0.05;
    private static final double SCROLL_V_FACTOR = 0.05;
    private static final int OFFSET = 30;
    private static GameScrollPane singleton;
    private List<Layer> layers;

    public static GameScrollPane get() {
        if (singleton == null) {
            singleton = new GameScrollPane(GameCanvas.get());
        }
        return singleton;
    }

    private GameScrollPane(Node node){
        super(node);
        Controller.setScrollPane(this);

        setSize();

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);

        setPannable(true);
        hookupEvents();
    }

    private void hookupEvents() {
        addEventFilter(ScrollEvent.SCROLL, Event::consume);
        addEventFilter(MouseEvent.ANY, event -> {
            if (!event.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    && !event.getEventType().equals(MouseEvent.MOUSE_CLICKED)
                    && event.getButton() != MouseButton.MIDDLE) event.consume();
        });

        CurrentLocation.get().locationProperty().addListener(observable -> {
            layers = getSortedLayers();
        });

        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode key = e.getCode();
            switch (key) {
                case UP, DOWN, LEFT, RIGHT -> e.consume();
                case PAGE_UP -> {
                    e.consume();
                    Layer layer = Controller.get().getCurrentLayer().getLayer();
                    Layer next = layer;
                    for (int i = 0; i < layers.size() - 1; i++) {
                        Layer current = layers.get(i);
                        if (current == layer) {
                            next = layers.get(i + 1);
                        }
                    }
                    Controller.get().getCurrentLayer().setLayer(next);
                }
                case PAGE_DOWN -> {
                    e.consume();
                    Layer layer = Controller.get().getCurrentLayer().getLayer();
                    Layer prev = layer;
                    for (int i = 1; i < layers.size(); i++) {
                        Layer current = layers.get(i);
                        if (current == layer) {
                            prev = layers.get(i - 1);
                        }
                    }
                    Controller.get().getCurrentLayer().setLayer(prev);
                }
            }
        });
    }

    private List<Layer> getSortedLayers() {
        List<Layer> layers = new ArrayList<>(Controller.get().getCurrentLocation().getLayers());
        return layers.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Layer::getLevel))
                .collect(Collectors.toList());
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
        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;
        int stageX = (int) GameStage.get().getX();
        int stageY = (int) GameStage.get().getY();
        int stageWidth = (int) GameStage.get().getWidth();
        int stageRight = stageX + stageWidth;
        int stageHeight = (int) GameStage.get().getHeight();
        int stageBottom = stageY + stageHeight;
        double canvasWidth = Controller.get().getCurrentLocation().getWidth();
        double canvasHeight = Controller.get().getCurrentLocation().getHeight();
        double normalizedScrollHFactor = SCROLL_H_FACTOR * getWidth() / canvasWidth;
        double normalizedScrollVFactor = SCROLL_V_FACTOR * getHeight() / canvasHeight;
        if (x < stageX+OFFSET && x >= stageX) {
            setHvalue(getHvalue() - normalizedScrollHFactor);
        } else
        if (x > stageRight-OFFSET && x <= stageRight) {
            setHvalue(getHvalue() + normalizedScrollHFactor);
        } else
        if (y < stageY+OFFSET && y >= stageY) {
            setVvalue(getVvalue() - normalizedScrollVFactor);
        } else
        if (y > stageBottom-OFFSET && y <= stageBottom) {
            setVvalue(getVvalue() + normalizedScrollVFactor);
        }
    }
}
