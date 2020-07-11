package game.view.stage;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
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

public class GameView extends CanvasView {
    private static final double OFFSET = 0.3 * Sizes.getMeter();

    private final Stage parent;
    private final GameController gameController = GameController.get();
    private final List<PosItem> items = new ArrayList<>(0);
    private final Coords currentPos = controller.getBoardPos();
    private final Coords mousePos = new Coords();
    private final Coords modifiedCoords = new Coords();
    private final Coords selFirst = new Coords(-1, -1, null);
    private final Coords selSecond = new Coords(-1, -1, null);
    private final BarView barView = new BarView(canvas);

    private List<Layer> layers;
    private EventHandler<MouseEvent> clickEvent;
    private EventHandler<KeyEvent> keyboardEvent;
    private DialogView dialogView;
    private InventoryView inventoryView;
    private boolean dialogStarted = true;
    private boolean inventoryStarted = true;
    private boolean constantWalk;
    private boolean selectionMode;

    public GameView(Stage parent) {
        super(new Canvas());
        this.parent = parent;
        hookUpEvents();
        defineRemovableEvents();
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
                dialogView = new DialogView(canvas, OFFSET);
            }
            dialogView.refresh();
            return;
        }
        if (!dialogStarted) {
            constantWalk = false;
            dialogStarted = true;
            hookUpRemovableEvents();
        }

        if (controller.isInventory()) {
            barView.refresh();
            if (inventoryStarted) {
                inventoryStarted = false;
                removeEvents();
                inventoryView = new InventoryView(canvas);
            }
            inventoryView.refresh();
            return;
        }
        if (!inventoryStarted) {
            constantWalk = false;
            inventoryStarted = true;
            hookUpRemovableEvents();
        }

        setSize();
        if (canvas.getWidth() == 0) {
            return;
        }
        updatePos();
        clear();
        sortItems();

        List<Creature> visibleControllables = getVisibleControllables(items);

        for (PosItem pi : items) {

            adjustCoverOpacity(visibleControllables, pi);

            final ItemType type = pi.getType();
            final Coords pos = pi.getPos();
            translateCoordsToScreenCoords(pos);
            final double x = modifiedCoords.x * Sizes.getMeter();
            final double y = modifiedCoords.y * Sizes.getMeter();

            switch (type) {
                case CREATURE -> drawCreatureSize((Creature) pi);
            }
            Image img = pi.getImage();
            double width = img.getWidth();
            double height = img.getHeight();

            double startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            if (width > canvas.getWidth()) {
                width = canvas.getWidth();
            }

            double startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }
            if (height > canvas.getHeight()) {
                height = canvas.getHeight();
            }

            double destX = 0;
            if (x > 0) {
                destX = x;
            }
            double destY = 0;
            if (y > 0) {
                destY = y;
            }
            gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);
            gc.setGlobalAlpha(1);
        }

        if (selectionMode) {
            drawSelection();
        }

        if (Settings.isShowBar()) {
            barView.refresh();
        }
    }

    private void sortItems() {
        Location location = controller.getCurrentLocation().getLocation();
        int level = controller.getCurrentLayer().getLevel();
        double canvasWidth = canvas.getWidth() / Sizes.getMeter();
        double canvasHeight = canvas.getHeight() / Sizes.getMeter();
        sortItems(location, currentPos, canvasWidth, canvasHeight,
                items, level);
    }

    private void drawSelection() {
        if (selFirst.x == -1 || selSecond.x == -1) {
            return;
        }
        final Coords first = selFirst;
        translateCoordsToScreenCoords(first);
        final int firstX = (int) (modifiedCoords.x * Sizes.getMeter());
        final int firstY = (int) (modifiedCoords.y * Sizes.getMeter());

        final Coords second = selSecond;
        translateCoordsToScreenCoords(second);
        final int secondX = (int) (modifiedCoords.x * Sizes.getMeter());
        final int secondY = (int) (modifiedCoords.y * Sizes.getMeter());

        int x = Math.min(firstX, secondX);
        int y = Math.min(firstY, secondY);
        int width = Math.abs(firstX - secondX);
        int height = Math.abs(firstY - secondY);
        gc.setStroke(Color.DARKOLIVEGREEN);
        gc.strokeRect(x, y, width, height);
    }

    private void translateCoordsToScreenCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(currentPos);
    }

    private Coords getMousePos(double mouseX, double mouseY, double left, double top) {
        mousePos.x = (mouseX - left) / Sizes.getMeter();
        mousePos.y = (mouseY - top) / Sizes.getMeter();
        return mousePos;
    }

    private void updatePos() {
        Coords posToCenter = controller.getPosToCenter();
        if (posToCenter.x != -1) {
            centerScreenOn(posToCenter);
            posToCenter.x = -1;
            return;
        }

        Bounds b = canvas.localToScreen(canvas.getBoundsInLocal());
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

        if (selectionMode) {
            Coords pos = getMousePos(x, y, left, top);
            modifiedCoords.x = pos.x;
            modifiedCoords.y = pos.y;
            modifiedCoords.add(currentPos);

            if (selFirst.x == -1) {
                selFirst.x = modifiedCoords.x;
                selFirst.y = modifiedCoords.y;
            } else {
                selSecond.x = modifiedCoords.x;
                selSecond.y = modifiedCoords.y;
            }
        }

        if (constantWalk) {
            Coords pos = getMousePos(x, y, left, top);
            modifiedCoords.x = pos.x;
            modifiedCoords.y = pos.y;
            modifiedCoords.add(currentPos);
            commandControllableGoTo(modifiedCoords);
        }

        if (Settings.isCenterOnPC()) {
            Location location = controller.getCurrentLocation().getLocation();
            List<Creature> controlledCreatures = board.getControlledCreatures(location);
            if (!controlledCreatures.isEmpty()) {
                Creature cr = controlledCreatures.get(0);
                if (cr != null) {
                    centerScreenOn(cr.getCenter());
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
        currentPos.y = Math.min(newY, locHeight - canvas.getHeight()/Sizes.getMeter());
    }

    private void scrollUp() {
        double newY = currentPos.y - Settings.getGameScrollSpeed();
        currentPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        double newX = currentPos.x + Settings.getGameScrollSpeed();
        currentPos.x = Math.min(newX, locWidth - canvas.getWidth()/Sizes.getMeter());
    }

    private void scrollLeft() {
        double newX = currentPos.x - Settings.getGameScrollSpeed();
        currentPos.x = Math.max(newX, 0);
    }

    private void drawCreatureSize(Creature cr) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.getCenter();
        translateCoordsToScreenCoords(centerBottomPos);
        double x = modifiedCoords.x * Sizes.getMeter();
        double y = modifiedCoords.y * Sizes.getMeter();
        switch (control) {
            case CONTROL -> gc.setStroke(Color.GREEN);
            case ENEMY -> gc.setStroke(Color.RED);
        }
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0 * Sizes.getMeter(), y - size.getHeight()/2.0 * Sizes.getMeter(),
                size.getWidth() * Sizes.getMeter(), size.getHeight() * Sizes.getMeter());
    }

    private void hookUpEvents() {
        canvas.setFocusTraversable(true);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.MIDDLE)) {
                e.consume();
                selectionMode = true;
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.MIDDLE)) {
                e.consume();
                selectionMode = false;
                boolean multiple = e.isShiftDown();
                synchronized (gameController.getGameRunner()) {
                    resolveSelection(selFirst, selSecond, multiple);
                }
                selFirst.x = -1;
                selFirst.y = -1;
                selSecond.x = -1;
                selSecond.y = -1;
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
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

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            Controller.get().clearHeroesPortraits();
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            Controller.get().clearHeroesPortraits();
        });
    }

    private void resolveSelection(Coords selFirst, Coords selSecond, boolean multiple) {
        Location location = controller.getCurrentLocation().getLocation();
        List<Creature> creatures = board.getControllablesWithinRectangle(selFirst, selSecond, location);
        creatures.forEach(c -> controller.getCreaturesToControl().add(c));
        if (!creatures.isEmpty() && !multiple) {
            board.looseCreaturesControl(location);
        }
    }

    private void defineRemovableEvents() {
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
                    Location location = controller.getCurrentLocation().getLocation();
                    board.looseCreaturesControl(location);
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
    }

    private void onMapPrimaryButtonClick(double x, double y, boolean multiple) {
        Coords pos = new Coords(x / Sizes.getMeter(), y / Sizes.getMeter(),
                Controller.get().getCurrentLocation().getLocation());
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.add(currentPos);
        Coords[] poss = new Coords[]{modifiedCoords};
        ItemType[] types = new ItemType[]{ItemType.CREATURE};
        Location location = Controller.get().getCurrentLocation().getLocation();
        PosItem pi = controller.getBoard().lookForContent(location, poss, types, true);
        if (pi != null) {
            ItemType type = pi.getType();
            boolean success = switch (type) {
                case CREATURE -> interact((Creature) pi, multiple);
                default -> false;
            };
            if (!success) {
                commandControllable(modifiedCoords);
                constantWalk = true;
            }
        } else {
            commandControllable(modifiedCoords);
            constantWalk = true;
        }
    }

    private void hookUpRemovableEvents() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private void removeEvents() {
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, clickEvent);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private boolean interact(Creature cr, boolean multiple) {
        CreatureControl control = cr.getControl();
        if (control == CONTROL) {
            cr.setControl(CONTROLLABLE);
            return true;
        } else if (control == CONTROLLABLE) {
            if (!multiple) {
                board.looseCreaturesControl(cr.getPos().getLocation());
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
        Location location = controller.getCurrentLocation().getLocation();
        List<Creature> controlled = Board.get().getControlledCreatures(location);
        if (controlled.isEmpty()) {
            return;
        }
        Creature active = controlled.get(0);
        controller.setCreatureToOpenInventory(active);
        controller.setInventory(true);
    }

    private void centerScreenOn(Coords posToCenter) {
        double canvasWidth = canvas.getWidth()/ Sizes.getMeter();
        double canvasHeight = canvas.getHeight()/ Sizes.getMeter();
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
    }

    private void commandControllable(Coords pos) {
        Location location = controller.getCurrentLocation().getLocation();
        board.getControlledCreatures(location)
                .forEach(c -> c.onInteractWith(pos));
    }

    private void commandControllableGoTo(Coords pos) {
        Location location = controller.getCurrentLocation().getLocation();
        board.getControlledCreatures(location)
                .forEach(c -> c.goTo(pos));
    }

    private void setSize() {
        Scene scene = canvas.getScene();
        if (scene == null) {
            return;
        }
        int locWidth = (int) (controller.getCurrentLocation().getWidth() * Sizes.getMeter());
        int locHeight = (int) (controller.getCurrentLocation().getHeight() * Sizes.getMeter());
        int resWidth = Settings.getResolutionWidth();
        int resHeight = Settings.getResolutionHeight();
        int sceneWidth = (int) canvas.getScene().getWidth();
        int sceneHeight = (int) canvas.getScene().getHeight();
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
            canvas.setWidth(maxWidth);
        } else {
            canvas.setWidth(locWidth);
        }

        if (locHeight >= maxHeight) {
            canvas.setHeight(maxHeight);
        } else {
            canvas.setHeight(locHeight);
        }
    }

    private void clear() {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private List<Layer> getSortedLayers() {
        List<Layer> layers = new ArrayList<>(controller.getCurrentLocation().getLayers());
        return layers.stream()
                .distinct()
                .sorted(Comparator.comparingInt(Layer::getLevel))
                .collect(Collectors.toList());
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Coords getCurrentPos() {
        return currentPos;
    }
}
