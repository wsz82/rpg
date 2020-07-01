package game.view.stage;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.sizes.Sizes;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;
import static io.wsz.model.item.CreatureControl.CONTROLLABLE;
import static javafx.scene.input.KeyCode.*;

public class GameView extends Canvas {
    private static final double OFFSET = 0.3 * Sizes.getMeter();
    private final Stage parent;
    private final Controller controller = Controller.get();
    private final Board board = controller.getBoard();
    private final Coords currentPos = controller.getBoardPos();
    private final GameController gameController = GameController.get();
    private final BarView barView = new BarView(this);
    private List<Layer> layers;
    private EventHandler<MouseEvent> clickEvent;
    private EventHandler<KeyEvent> keyboardEvent;
    private DialogView dialogView;
    private boolean dialogStarted = true;
    private boolean constantWalk;

    public GameView(Stage parent) {
        this.parent = parent;
        defineEvents();
        hookUpRemovableEvents();
    }

    public void refresh() {
        if (parent.isIconified()) {
            return;
        }
        if (gameController.isDialog()) {
            if (dialogStarted) {
                dialogStarted = false;
                removeEvents();
                dialogView = new DialogView(this, OFFSET);
            }
            dialogView.refresh();
            return;
        }
        if (!dialogStarted) {
            dialogStarted = true;
            hookUpRemovableEvents();
        }
        setSize();
        if (getWidth() == 0) {
            return;
        }
        updatePos();

        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        double leftX = currentPos.x;
        double rightX = leftX + getWidth()/Sizes.getMeter();
        double topY = currentPos.y;
        double bottomY = topY + getHeight()/Sizes.getMeter();

        List<PosItem> items = controller.getCurrentLocation().getItems();
        Board.get().sortItems(items);
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    Coords pos = pi.getPos();
                    Image img = pi.getImage();
                    double piLeftX = pos.x;
                    double piRightX = piLeftX + img.getWidth()/Sizes.getMeter();
                    double piTopY = pos.y;
                    double piBottomY = piTopY + img.getHeight()/Sizes.getMeter();
                    return io.wsz.model.stage.Comparator.doOverlap(
                            leftX, topY, rightX, bottomY,
                            piLeftX, piTopY, piRightX, piBottomY);
                })
                .filter(pi -> pi.getLevel() <= controller.getCurrentLayer().getLevel())    //TODO
                .collect(Collectors.toList());
        for (PosItem pi : items) {
            final ItemType type = pi.getType();
            final Coords pos = pi.getPos();
            Coords translated = pos.subtract(currentPos);
            final int x = (int) (translated.x * Sizes.getMeter());
            final int y = (int) (translated.y * Sizes.getMeter());

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

        if (Settings.isShowBar()) {
            barView.refresh();
        }
    }

    private Coords getMouseCoords(double mouseX, double mouseY, double left, double top) {
        double x = (mouseX - left) / Sizes.getMeter();
        double y = (mouseY - top) / Sizes.getMeter();
        return new Coords(x, y);
    }

    private void updatePos() {
        Coords posToCenter = controller.getPosToCenter();
        if (posToCenter != null) {
            centerScreenOn(posToCenter);
            controller.setPosToCenter(null);
            return;
        }

        Bounds b = localToScreen(getBoundsInLocal());
        if (b == null) {
            return;
        }
        double left = b.getMinX();
        double top = b.getMinY();
        double right = b.getMaxX();
        double bottom = b.getMaxY();
        double locWidth = controller.getCurrentLocation().getWidth();
        double locHeight = controller.getCurrentLocation().getHeight();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < left || x > right || y < top || y > bottom) {
            return;
        }

        if (constantWalk) {
            Coords pos = getMouseCoords(x, y, left, top);
            Coords translated = pos.add(currentPos);
            commandControllableGoTo(translated);
        }

        if (Settings.isCenterOnPC()) {
            List<Creature> controlledCreatures = board.getControlledCreatures();
            if (!controlledCreatures.isEmpty()) {
                Creature cr = controlledCreatures.get(0);
                if (cr != null) {
                    centerScreenOn(cr.getCenterBottomPos());
                    return;
                }
            }
        }

        if (x >= left+OFFSET && x <= right-OFFSET
                && y >= top+OFFSET && y <= bottom-OFFSET) {
            return;
        }

        if (x < left+OFFSET && x >= left
                && y > top+OFFSET && y < bottom-OFFSET) {
            scrollLeft();
        } else
        if (x > right-OFFSET && x <= right
                && y > top+OFFSET && y < bottom-OFFSET) {
            scrollRight(locWidth);
        } else
        if (y < top+OFFSET && y >= top
                && x > left+OFFSET && x < right-OFFSET) {
            scrollUp();
        } else
        if (y > bottom-OFFSET && y <= bottom
                && x > left+OFFSET && x < right-OFFSET) {
            scrollDown(locHeight);
        } else
        if (x < left+OFFSET && x >= left
                && y >= top && y < top+OFFSET) {
            scrollLeft();
            scrollUp();
        } else
        if (x > right-OFFSET && x <= right
                && y >= top && y < top+OFFSET) {
            scrollRight(locWidth);
            scrollUp();
        } else
        if (x < left+OFFSET && x >= left
                && y >= bottom-OFFSET && y < bottom) {
            scrollLeft();
            scrollDown(locHeight);
        } else
        if (x > right-OFFSET && x <= right
                && y >= bottom-OFFSET && y < bottom) {
            scrollRight(locWidth);
            scrollDown(locHeight);
        }
    }

    private void scrollDown(double locHeight) {
        double newY = currentPos.y + Settings.getGameScrollSpeed();
        currentPos.y = Math.min(newY, locHeight - getHeight()/Sizes.getMeter());
    }

    private void scrollUp() {
        double newY = currentPos.y - Settings.getGameScrollSpeed();
        currentPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        double newX = currentPos.x + Settings.getGameScrollSpeed();
        currentPos.x = Math.min(newX, locWidth - getWidth()/Sizes.getMeter());
    }

    private void scrollLeft() {
        double newX = currentPos.x - Settings.getGameScrollSpeed();
        currentPos.x = Math.max(newX, 0);
    }

    private void drawCreatureSize(Creature cr, GraphicsContext gc) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.getCenterBottomPos();
        Coords translated = centerBottomPos.subtract(currentPos);
        double x = translated.x * Sizes.getMeter();
        double y = translated.y * Sizes.getMeter();
        switch (control) {
            case CONTROL -> gc.setStroke(Color.GREEN);
            case ENEMY -> gc.setStroke(Color.RED);
        }
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0 * Sizes.getMeter(), y - size.getHeight()/2.0 * Sizes.getMeter(),
                size.getWidth() * Sizes.getMeter(), size.getHeight() * Sizes.getMeter());
    }

    private void defineEvents() {
        setFocusTraversable(true);
        clickEvent = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                if (Settings.isShowBar()) {
                    double barLeft = barView.getLeft();
                    if (e.getX() > barLeft) {
                        return;
                    }
                }
                e.consume();
                synchronized (gameController.getGameRunner()) {
                    onMapPrimaryButtonClick(e.getX(), e.getY(), e.isShiftDown());
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                synchronized (gameController.getGameRunner()) {
                    board.looseCreaturesControl();
                }
            }
        };
        keyboardEvent = e -> {
            KeyCode key = e.getCode();
            List<KeyCode> barKeys = List.of(DIGIT1, DIGIT2, DIGIT3, DIGIT4, DIGIT5 ,DIGIT6);
            if (barKeys.contains(key)) {
                return;
            }
            switch (key) {
                case P -> {
                    e.consume();
                    Settings.setShowBar(!Settings.isShowBar());
                }
                case I -> {
                    e.consume();
                    synchronized (gameController.getGameRunner()) {
                        openInventory();
                    }
                }
                case SPACE -> {
                    e.consume();
                    handlePause();
                }
                case PAGE_UP -> {
                    e.consume();
                    synchronized (gameController.getGameRunner()) {
                        Layer layer = controller.getCurrentLayer().getLayer();
                        Layer next = layer;
                        for (int i = 0; i < layers.size() - 1; i++) {
                            Layer current = layers.get(i);
                            if (current == layer) {
                                next = layers.get(i + 1);
                            }
                        }
                        controller.getCurrentLayer().setLayer(next);
                    }
                }
                case PAGE_DOWN -> {
                    e.consume();
                    synchronized (gameController.getGameRunner()) {
                        Layer layer = controller.getCurrentLayer().getLayer();
                        Layer prev = layer;
                        for (int i = 1; i < layers.size(); i++) {
                            Layer current = layers.get(i);
                            if (current == layer) {
                                prev = layers.get(i - 1);
                            }
                        }
                        controller.getCurrentLayer().setLayer(prev);
                    }
                }
            }
        };

        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                constantWalk = false;
            }
        });

        CurrentLocation.get().locationProperty().addListener(observable -> {
            layers = getSortedLayers();
            constantWalk = false;
        });

        widthProperty().addListener((observable, oldValue, newValue) -> {
            Controller.get().clearHeroesPortraits();
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            Controller.get().clearHeroesPortraits();
        });
    }

    private void onMapPrimaryButtonClick(double x, double y, boolean multiple) {
        Coords pos = new Coords(x / Sizes.getMeter(), y / Sizes.getMeter());
        Coords translated = pos.add(currentPos);
        Coords[] poss = new Coords[]{translated};
        ItemType[] types = new ItemType[]{ItemType.CREATURE};
        PosItem pi = controller.getBoard().lookForContent(poss, types, true);
        if (pi != null) {
            ItemType type = pi.getType();
            boolean success = switch (type) {
                case CREATURE -> interact((Creature) pi, multiple);
                default -> false;
            };
            if (!success) {
                commandControllable(translated);
                constantWalk = true;
            }
        } else {
            commandControllable(translated);
            constantWalk = true;
        }
    }

    private void hookUpRemovableEvents() {
        addEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        addEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private void removeEvents() {
        removeEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        removeEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private boolean interact(Creature cr, boolean multiple) {
        CreatureControl control = cr.getControl();
        if (control == CONTROL) {
            cr.setControl(CONTROLLABLE);
            return true;
        } else if (control == CONTROLLABLE) {
            if (!multiple) {
                board.looseCreaturesControl();
            }
            cr.setControl(CONTROL);
            return true;
        }
        return false;
    }

    private void handlePause() {
        boolean isGame = gameController.isGame();
        gameController.setGame(!isGame);
    }

    private void openInventory() {
        List<Creature> controlled = Board.get().getControlledCreatures();
        if (controlled.isEmpty()) {
            return;
        }
        Creature active = controlled.get(0);
        gameController.openInventory(active, null);
    }

    private void centerScreenOn(Coords posToCenter) {
        double canvasWidth = getWidth()/ Sizes.getMeter();
        double canvasHeight = getHeight()/ Sizes.getMeter();
        double x = posToCenter.x - canvasWidth/2;
        double y = posToCenter.y - canvasHeight/2;
        double locWidth = controller.getCurrentLocation().getWidth();
        double locHeight = controller.getCurrentLocation().getHeight();
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
        controller.setPosToCenter(null);
    }

    private void commandControllable(Coords pos) {
        board.getControlledCreatures()
                .forEach(c -> c.onInteractWith(pos));
    }

    private void commandControllableGoTo(Coords pos) {
        board.getControlledCreatures()
                .forEach(c -> c.goTo(pos));
    }

    private void setSize() {
        Scene scene = getScene();
        if (scene == null) {
            return;
        }
        int locWidth = (int) (controller.getCurrentLocation().getWidth() * Sizes.getMeter());
        int locHeight = (int) (controller.getCurrentLocation().getHeight() * Sizes.getMeter());
        int resWidth = Settings.getResolutionWidth();
        int resHeight = Settings.getResolutionHeight();
        int sceneWidth = (int) getScene().getWidth();
        int sceneHeight = (int) getScene().getHeight();
        double maxWidth = sceneWidth;
        double maxHeight = sceneHeight;

        if (resWidth != sceneWidth || resHeight != sceneHeight) {
            double rW = (double) sceneWidth/resWidth;
            double rH = (double) sceneHeight/resHeight;
            if (rW > rH) {
                maxHeight = sceneHeight;
                maxWidth = resWidth * rH;
            } else {
                maxWidth = sceneWidth;
                maxHeight = resHeight * rW;
            }
        }

        if (locWidth >= maxWidth) {
            setWidth(maxWidth);
        } else {
            setWidth(locWidth);
        }

        if (locHeight >= maxHeight) {
            setHeight(maxHeight);
        } else {
            setHeight(locHeight);
        }
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    private List<Layer> getSortedLayers() {
        List<Layer> layers = new ArrayList<>(controller.getCurrentLocation().getLayers());
        return layers.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Layer::getLevel))
                .collect(Collectors.toList());
    }

    public Coords getCurrentPos() {
        return currentPos;
    }
}
