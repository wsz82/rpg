package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameCanvas extends Canvas {
    private static final int OFFSET = 30;
    private static final int SCROLL = 20;
    private final Board board = Controller.get().getBoard();
    private final Coords currentPos = Controller.get().getBoardPos();
    private final GameStage gameStage;
    private List<Layer> layers;

    public GameCanvas(GameStage gameStage){
        this.gameStage = gameStage;
        hookupEvents();
    }

    public void refresh() {
        setSize();
        updatePos();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        int leftX = currentPos.x;
        int rightX = leftX + (int) getWidth();
        int topY = currentPos.y;
        int bottomY = topY + (int) getHeight();

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        Board.get().sortItems(items);
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    Coords pos = pi.getPos();
                    Image img = pi.getImage();
                    int piLeftX = pos.x;
                    int piRightX = piLeftX + (int) img.getWidth();
                    int piTopY = pos.y;
                    int piBottomY = piTopY + (int) img.getHeight();
                    return io.wsz.model.stage.Comparator.doOverlap(
                            leftX, topY, rightX, bottomY,
                            piLeftX, piTopY, piRightX, piBottomY);
                })
                .filter(pi -> pi.getLevel() <= Controller.get().getCurrentLayer().getLevel())    //TODO
                .collect(Collectors.toList());
        for (PosItem pi : items) {
            final ItemType type = pi.getType();
            final Coords pos = pi.getPos();
            Coords translated = pos.subtract(currentPos);
            final int x = translated.x;
            final int y = translated.y;

            if (pi.getVisible()) {
                switch (type) {
                    case CREATURE -> {
                        drawCreatureSize((Creature) pi, gc);
                    }
                }
                gc.drawImage(pi.getImage(), x, y);
            }
        }
    }

    private void updatePos() {
        Bounds b = localToScreen(getBoundsInLocal());
        if (b == null) {
            return;
        }
        int leftX = (int) b.getMinX();
        int topY = (int) b.getMinY();
        int rightX = (int) b.getMaxX();
        int bottomY = (int) b.getMaxY();
        int locWidth = Controller.get().getCurrentLocation().getWidth();
        int locHeight = Controller.get().getCurrentLocation().getHeight();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < leftX+OFFSET && x >= leftX
                && y > topY + OFFSET && y < bottomY - OFFSET) {
            scrollLeft();
        } else
        if (x > rightX-OFFSET && x <= rightX
                && y > topY + OFFSET && y < bottomY - OFFSET) {
            scrollRight(locWidth);
        } else
        if (y < topY+OFFSET && y >= topY
                && x > leftX + OFFSET && x < rightX - OFFSET) {
            scrollUp();
        } else
        if (y > bottomY-OFFSET && y <= bottomY
                && x > leftX + OFFSET && x < rightX - OFFSET) {
            scrollDown(locHeight);
        } else
        if (x < leftX+OFFSET && x >= leftX
                && y >= topY && y < topY+OFFSET) {
            scrollLeft();
            scrollUp();
        } else
        if (x > rightX-OFFSET && x <= rightX
                && y >= topY && y < topY+OFFSET) {
            scrollRight(locWidth);
            scrollUp();
        } else
        if (x < leftX+OFFSET && x >= leftX
                && y >= bottomY - OFFSET && y < bottomY) {
            scrollLeft();
            scrollDown(locHeight);
        } else
        if (x > rightX-OFFSET && x <= rightX
                && y >= bottomY - OFFSET && y < bottomY) {
            scrollRight(locWidth);
            scrollDown(locHeight);
        }
    }

    private void scrollDown(int locHeight) {
        int newY = currentPos.y = currentPos.y + SCROLL;
        currentPos.y = Math.min(newY, locHeight - (int) getHeight());
    }

    private void scrollUp() {
        int newY = currentPos.y = currentPos.y - SCROLL;
        currentPos.y = Math.max(newY, 0);
    }

    private void scrollRight(int locWidth) {
        int newX = currentPos.x + SCROLL;
        currentPos.x = Math.min(newX, locWidth - (int) getWidth());
    }

    private void scrollLeft() {
        int newX = currentPos.x - SCROLL;
        currentPos.x = Math.max(newX, 0);
    }

    private void drawCreatureSize(Creature cr, GraphicsContext gc) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.posToCenter();
        Coords translated = centerBottomPos.subtract(currentPos);
        int x = translated.x;
        int y = translated.y;
        switch (control) {
            case CONTROL -> gc.setStroke(Color.GREEN);
            case ENEMY -> gc.setStroke(Color.RED);
        }
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0, y - size.getHeight()/2.0, size.getWidth(), size.getHeight());
    }

    private void hookupEvents() {
        setFocusTraversable(true);
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Coords pos = new Coords((int) e.getX(), (int) e.getY());
                Coords translated = pos.add(currentPos);
                Coords[] poss = new Coords[] {translated};
                ItemType[] types = new ItemType[] {ItemType.CREATURE};
                PosItem pi = Controller.get().getBoard().lookForContent(poss, types, true);
                if (pi != null) {
                    ItemType type = pi.getType();
                    switch (type) {
                        case CREATURE -> ((Creature) pi).interact();
                    }
                } else {
                    commandControllable(translated);
                }
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                board.getControlledCreatures()
                        .forEach(Creature::loseControl);
            }

        });

        Controller.get().centerPosProperty().addListener((observable, oldValue, newValue) -> {
            int x = newValue.x - (int) getWidth()/2;
            int y = newValue.y - (int) getHeight()/2;
            int locWidth = Controller.get().getCurrentLocation().getWidth();
            int locHeight = Controller.get().getCurrentLocation().getHeight();
            if (x > locWidth - getWidth()) {
                currentPos.x = locWidth - (int) getWidth();
            } else {
                currentPos.x = Math.max(x, 0);
            }
            if (y > locHeight - getHeight()) {
                currentPos.y = locHeight - (int) getHeight();
            } else {
                currentPos.y = Math.max(y, 0);
            }
        });

        CurrentLocation.get().locationProperty().addListener(observable -> {
            layers = getSortedLayers();
        });

        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode key = e.getCode();
            switch (key) {
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

    private void commandControllable(Coords pos) {
        board.getControlledCreatures()
                .forEach(c -> c.onInteractWith(pos));
    }

    private void setSize() {
        Scene scene = getScene();
        if (scene == null) {
            return;
        }
        int locWidth = Controller.get().getCurrentLocation().getWidth();
        int locHeight = Controller.get().getCurrentLocation().getHeight();
        double maxWidth = getScene().getWidth();
        double maxHeight = getScene().getHeight();
        if (locWidth >= maxWidth) {
            setWidth(maxWidth);
        } else {
            setWidth(locWidth);
            setTranslateX((maxWidth - locWidth)/2);
        }
        if (locHeight >= maxHeight) {
            setHeight(maxHeight);
        } else {
            setHeight(locHeight);
            setTranslateY((maxHeight - locHeight)/2);
        }
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    private List<Layer> getSortedLayers() {
        List<Layer> layers = new ArrayList<>(Controller.get().getCurrentLocation().getLayers());
        return layers.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Layer::getLevel))
                .collect(Collectors.toList());
    }

    public Coords getCurrentPos() {
        return currentPos;
    }
}
