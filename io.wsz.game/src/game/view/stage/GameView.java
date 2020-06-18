package game.view.stage;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
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

import static io.wsz.model.Constants.METER;

public class GameView extends Canvas {
    private static final double OFFSET = 0.3 * METER;
    private static final double SCROLL = 0.2;
    private final Board board = Controller.get().getBoard();
    private final Coords currentPos = Controller.get().getBoardPos();
    private final EventHandler<KeyEvent> inventoryEvent = e -> {
        if (e.getCode().equals(KeyCode.I)) {
            e.consume();
            openInventory();
        }
    };
    private List<Layer> layers;

    public GameView() {
        hookupEvents();
    }

    public void refresh() {
        setSize();
        updatePos();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        double leftX = currentPos.x;
        double rightX = leftX + getWidth()/METER;
        double topY = currentPos.y;
        double bottomY = topY + getHeight()/METER;

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        Board.get().sortItems(items);
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    Coords pos = pi.getPos();
                    Image img = pi.getImage();
                    double piLeftX = pos.x;
                    double piRightX = piLeftX + img.getWidth();
                    double piTopY = pos.y;
                    double piBottomY = piTopY + img.getHeight();
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
            final int x = (int) (translated.x * METER);
            final int y = (int) (translated.y * METER);

            if (pi.getVisible()) {
                switch (type) {
                    case CREATURE -> {
                        drawCreatureSize((Creature) pi, gc);
                    }
                }
                Image img = pi.getImage();
                int width = (int) img.getWidth();
                int height = (int) img.getHeight();

                int startX = 0;
                if (x < 0) {
                    startX = -x;
                    width = x + width;
                }
                int startY = 0;
                if (y < 0) {
                    startY = -y;
                    height = y + height;
                }

                int destX = 0;
                if (x > 0) {
                    destX = x;
                }
                int destY = 0;
                if (y > 0) {
                    destY = y;
                }
                gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);
            }
        }
    }

    private void updatePos() {

        Coords posToCenter = Controller.get().getPosToCenter();
        if (posToCenter != null) {
            centerScreenOn(posToCenter);
            Controller.get().setPosToCenter(null);
        }

        Bounds b = localToScreen(getBoundsInLocal());
        if (b == null) {
            return;
        }
        int leftX = (int) b.getMinX();
        int topY = (int) b.getMinY();
        int rightX = (int) b.getMaxX();
        int bottomY = (int) b.getMaxY();
        double locWidth = Controller.get().getCurrentLocation().getWidth();
        double locHeight = Controller.get().getCurrentLocation().getHeight();

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

    private void scrollDown(double locHeight) {
        double newY = currentPos.y = currentPos.y + SCROLL;
        currentPos.y = Math.min(newY, locHeight - getHeight()/METER);
    }

    private void scrollUp() {
        double newY = currentPos.y = currentPos.y - SCROLL;
        currentPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        double newX = currentPos.x + SCROLL;
        currentPos.x = Math.min(newX, locWidth - getWidth()/METER);
    }

    private void scrollLeft() {
        double newX = currentPos.x - SCROLL;
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
        double x = translated.x * METER;
        double y = translated.y * METER;
        switch (control) {
            case CONTROL -> gc.setStroke(Color.GREEN);
            case ENEMY -> gc.setStroke(Color.RED);
        }
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0 * METER, y - size.getHeight()/2.0 * METER,
                size.getWidth() * METER, size.getHeight() * METER);
    }

    private void hookupEvents() {
        setFocusTraversable(true);
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Coords pos = new Coords(e.getX() / METER, e.getY() / METER);
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

        CurrentLocation.get().locationProperty().addListener(observable -> {
            layers = getSortedLayers();
        });

        addEventHandler(KeyEvent.KEY_RELEASED, inventoryEvent);

        addEventHandler(KeyEvent.KEY_RELEASED, e -> {
            KeyCode key = e.getCode();
            switch (key) {
                case SPACE -> handlePause();
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

    private void handlePause() {
        boolean isGame = GameController.get().isGame();
        GameController.get().setGame(!isGame);
    }

    private void openInventory() {
        List<Creature> controlled = Board.get().getControlledCreatures();
        if (controlled.isEmpty()) {
            return;
        }
        Creature active = controlled.get(0);
        GameController.get().openInventory(active);
    }

    private void centerScreenOn(Coords posToCenter) {
        double canvasWidth = getWidth()/METER;
        double canvasHeight = getHeight()/METER;
        double x = posToCenter.x - canvasWidth/2;
        double y = posToCenter.y - canvasHeight/2;
        double locWidth = Controller.get().getCurrentLocation().getWidth();
        double locHeight = Controller.get().getCurrentLocation().getHeight();
        if (x > locWidth - canvasWidth) {
            currentPos.x = locWidth - canvasWidth;
        } else {
            currentPos.x = Math.max(x, 0);
        }
        if (y > locHeight - canvasHeight) {
            currentPos.y = locHeight - canvasHeight;
        } else {
            currentPos.y = Math.max(y, 0);
        }
        Controller.get().setPosToCenter(null);
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
        double locWidth = Controller.get().getCurrentLocation().getWidth() * METER;
        double locHeight = Controller.get().getCurrentLocation().getHeight() * METER;
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
