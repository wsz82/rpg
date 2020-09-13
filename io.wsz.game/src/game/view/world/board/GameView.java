package game.view.world.board;

import game.model.GameController;
import game.model.setting.Settings;
import game.model.textures.Cursor;
import game.model.world.GameRunner;
import game.view.world.CanvasView;
import game.view.world.FoggableDelegate;
import game.view.world.dialog.DialogView;
import game.view.world.inventory.InventoryView;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Container;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.ImageCursor;
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
import static io.wsz.model.item.ItemType.*;
import static javafx.scene.input.KeyCode.*;

public class GameView extends CanvasView {
    private static final double OFFSET = 0.3 * Sizes.getMeter();
    private static final ItemType[] CURSOR_TYPES =
            new ItemType[] {LANDSCAPE, CREATURE, CONTAINER, WEAPON, INDOOR, OUTDOOR};
    private static final ItemType[] PRIMARY_TYPES =
            new ItemType[] {CREATURE, CONTAINER, WEAPON, INDOOR, OUTDOOR};
    private static final ItemType[] SECONDARY_TYPES =
            new ItemType[] {INDOOR, OUTDOOR, CONTAINER};
    private static final ItemType[] CREATURE_TYPE = new ItemType[] {CREATURE};
    private static final Coords TEMP = new Coords();

    private final Stage parent;
    private final List<PosItem> items = new ArrayList<>(0);
    private final Coords mousePos = new Coords();
    private final Coords selFirst = new Coords(-1, -1);
    private final Coords selSecond = new Coords(-1, -1);
    private final BarView barView;
    private final Coords curPos;
    private final FoggableDelegate foggableDelegate;

    private List<Layer> layers;
    private long nextAvailableClickTime;
    private EventHandler<MouseEvent> clickEvent;
    private EventHandler<KeyEvent> keyboardEvent;
    private DialogView dialogView;
    private InventoryView inventoryView;
    private boolean canStartDialog = true;
    private boolean canOpenInventory = true;
    private boolean isRefreshedOnce;
    private boolean isSelectionMode;

    public GameView(Stage parent, GameController gameController) {
        super(new Canvas(), gameController);
        this.parent = parent;
        barView = new BarView(canvas, gameController);
        curPos = controller.getCurPos();
        foggableDelegate = new FoggableDelegate(gameController, canvas, curPos);
        hookUpEvents();
        defineRemovableEvents();
        hookUpRemovableEvents();
    }

    public void refresh() {
        if (parent.isIconified()) {
            return;
        }
        setSize();
        if (canvas.getWidth() == 0) {
            return;
        }

        if (tryStartDialog()) return;
        if (tryOpenInventory()) return;
        updatePos();
        clearBackground();
        sortItems();

        Location location = controller.getCurrentLocation().getLocation();
        List<Creature> heroes = board.getControlledAndControllableCreatures(location);

        drawItems(heroes);

        drawFog();

        if (isSelectionMode) {
            drawSelection();
        }
        if (gameController.getSettings().isShowBar()) {
            barView.refresh();
        }
    }

    void drawFog() {
        int meter = Sizes.getMeter();
        double width = canvas.getWidth() / meter;
        double height = canvas.getHeight() / meter;
        foggableDelegate.drawFog(width, height);
    }

    boolean tryOpenInventory() {
        if (controller.isInventory()) {
            barView.refresh();
            if (canOpenInventory) {
                canOpenInventory = false;
                removeRemovableEvents();
                inventoryView = new InventoryView(canvas, gameController);
                ImageCursor main = gameController.getCursor().getMain();
                setCursor(main);
            }
            inventoryView.refresh();
            return true;
        }
        if (!canOpenInventory) {
            canOpenInventory = true;
            hookUpRemovableEvents();
        }
        return false;
    }

    private boolean tryStartDialog() {
        if (gameController.isDialog()) {
            if (isRefreshedOnce) {
                if (canStartDialog) {
                    canStartDialog = false;
                    removeRemovableEvents();
                    DialogMemento dialogMemento = controller.getDialogMemento();
                    dialogView = new DialogView(canvas, gameController, OFFSET, dialogMemento);
                }
                dialogView.refresh();
                return true;
            } else {
                isRefreshedOnce = true;
            }
        }
        if (!canStartDialog) {
            canStartDialog = true;
            isRefreshedOnce = false;
            hookUpRemovableEvents();
        }
        return false;
    }

    private void drawItems(List<Creature> heroes) {
        for (PosItem pi : items) {
            if (heroes != null) {
                adjustCoverOpacity(heroes, pi);
            }

            Coords pos = pi.getPos();
            Coords translatedPos = translateCoordsToScreenCoords(pos);
            int meter = Sizes.getMeter();
            double x = translatedPos.x * meter;
            double y = translatedPos.y * meter;

            if (pi instanceof Creature) {
                Creature cr = (Creature) pi;
                drawCreatureBase(cr);
            } else if (pi instanceof Equipment) {
                Equipment e = (Equipment) pi;
                setDropAnimationPos(e);
            }
            Image img = pi.getImage().getFxImage();

            cutImageAndDraw(x, y, img, 0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setGlobalAlpha(1);
        }
    }

    private void sortItems() {
        Location location = controller.getCurrentLocation().getLocation();
        int level = controller.getCurrentLayer().getLevel();
        int meter = Sizes.getMeter();
        double canvasWidth = canvas.getWidth() / meter;
        double canvasHeight = canvas.getHeight() / meter;
        sortItems(location, curPos.x, curPos.y, canvasWidth, canvasHeight, items, level);
    }

    private void drawSelection() {
        if (selFirst.x == -1 || selSecond.x == -1) {
            return;
        }
        Coords translatedPos = translateCoordsToScreenCoords(selFirst);
        int meter = Sizes.getMeter();
        final int firstX = (int) (translatedPos.x * meter);
        final int firstY = (int) (translatedPos.y * meter);

        translatedPos = translateCoordsToScreenCoords(selSecond);
        final int secondX = (int) (translatedPos.x * meter);
        final int secondY = (int) (translatedPos.y * meter);

        int x = Math.min(firstX, secondX);
        int y = Math.min(firstY, secondY);
        int width = Math.abs(firstX - secondX);
        int height = Math.abs(firstY - secondY);
        gc.setStroke(Color.DARKOLIVEGREEN);
        gc.strokeRect(x, y, width, height);
    }

    private Coords translateCoordsToScreenCoords(Coords pos) {
        TEMP.x = pos.x;
        TEMP.y = pos.y;
        TEMP.subtract(curPos);
        return TEMP;
    }

    private Coords getMousePos(double mouseX, double mouseY, double left, double top) {
        int meter = Sizes.getMeter();
        mousePos.x = (mouseX - left) / meter;
        mousePos.y = (mouseY - top) / meter;
        return mousePos;
    }

    private void updatePos() {
        Coords posToCenter = controller.getPosToCenter();
        int meter = Sizes.getMeter();
        double canvasWidth = canvas.getWidth();
        double canvasMeterWidth = canvasWidth / meter;
        double canvasMeterHeight = canvas.getHeight() / meter;
        Settings settings = gameController.getSettings();
        if (posToCenter.x != -1) {
            double widthCorrection = 0;
            if (settings.isShowBar()) {
                widthCorrection = barView.getWidth() / meter;
            }
            board.centerScreenOn(posToCenter, canvasMeterWidth, canvasMeterHeight, -widthCorrection);
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
        if (settings.isShowBar()) {
            right -= barView.getWidth();
        }
        double bottom = b.getMaxY();
        CurrentLocation currentLocation = controller.getCurrentLocation();
        double locWidth = currentLocation.getWidth();
        double locHeight = currentLocation.getHeight();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < left || x > right || y < top || y > bottom) {
            ImageCursor main = gameController.getCursor().getMain();
            setCursor(main);
            return;
        }

        Coords pos = getMapCoords(x, y, left, top);
        if (isSelectionMode) {
            if (selFirst.x == -1) {
                selFirst.x = pos.x;
                selFirst.y = pos.y;
            } else {
                selSecond.x = pos.x;
                selSecond.y = pos.y;
            }
        }

        Location location = currentLocation.getLocation();

        if (settings.isCenterOnPC()) {
            centerOnPC(meter, canvasMeterWidth, canvasMeterHeight, settings, location);
        }

        drawAppropriateCursor(pos, location);

        if (x >= left+OFFSET && x <= right-OFFSET
                && y >= top+OFFSET && y <= bottom-OFFSET) {
            return;
        }

        if (x < left+OFFSET && x >= left
                && y > top+OFFSET && y < bottom-OFFSET) {
            ImageCursor cursor = gameController.getCursor().getLeft();
            setCursor(cursor);
            scrollLeft();
        } else
        if (x > right-OFFSET && x <= right
                && y > top+OFFSET && y < bottom-OFFSET) {
            ImageCursor cursor = gameController.getCursor().getRight();
            setCursor(cursor);
            scrollRight(locWidth);
        } else
        if (y < top+OFFSET && y >= top
                && x > left+OFFSET && x < right-OFFSET) {
            ImageCursor cursor = gameController.getCursor().getUp();
            setCursor(cursor);
            scrollUp();
        } else
        if (y > bottom-OFFSET && y <= bottom
                && x > left+OFFSET && x < right-OFFSET) {
            ImageCursor cursor = gameController.getCursor().getDown();
            setCursor(cursor);
            scrollDown(locHeight);
        } else
        if (x < left+OFFSET && x >= left
                && y >= top && y < top+OFFSET) {
            ImageCursor cursor = gameController.getCursor().getLeftUpCursor();
            setCursor(cursor);
            scrollLeft();
            scrollUp();
        } else
        if (x > right-OFFSET && x <= right
                && y >= top && y < top+OFFSET) {
            ImageCursor cursor = gameController.getCursor().getRightUpCursor();
            setCursor(cursor);
            scrollRight(locWidth);
            scrollUp();
        } else
        if (x < left+OFFSET && x >= left
                && y >= bottom-OFFSET && y < bottom) {
            ImageCursor cursor = gameController.getCursor().getLeftDownCursor();
            setCursor(cursor);
            scrollLeft();
            scrollDown(locHeight);
        } else
        if (x > right-OFFSET && x <= right
                && y >= bottom-OFFSET && y < bottom) {
            ImageCursor cursor = gameController.getCursor().getRightDownCursor();
            setCursor(cursor);
            scrollRight(locWidth);
            scrollDown(locHeight);
        }
    }

    private void centerOnPC(int meter, double canvasMeterWidth, double canvasMeterHeight, Settings settings, Location location) {
        List<Creature> controlledCreatures = board.getControlledCreatures(location);
        if (!controlledCreatures.isEmpty()) {
            Creature cr = controlledCreatures.get(0);
            if (cr != null) {
                double widthCorrection = 0;
                if (settings.isShowBar()) {
                    widthCorrection = barView.getWidth() / meter;
                }
                board.centerScreenOn(cr.getCenter(), canvasMeterWidth, canvasMeterHeight, -widthCorrection);
            }
        }
    }

    private void drawAppropriateCursor(Coords pos, Location location) {
        int level = controller.getCurrentLayer().getLevel();
        PosItem item = board.lookForItem(location, pos.x, pos.y, level, CURSOR_TYPES, false);
        if (item instanceof Landscape) {
            ImageCursor cursor = gameController.getCursor().getMain();
            setCursor(cursor);
        } else if (item instanceof InDoor || item instanceof OutDoor) {
            Openable door = (Openable) item;
            ImageCursor imageCursor;
            Cursor cursor = gameController.getCursor();
            if (door.isOpen()) {
                imageCursor = cursor.getOpenDoorCursor();
            } else {
                imageCursor = cursor.getClosedDoorCursor();
            }
            setCursor(imageCursor);
        } else if (item instanceof Container) {
            Openable container = (Openable) item;
            ImageCursor imageCursor;
            Cursor cursor = gameController.getCursor();
            if (container.isOpen()) {
                imageCursor = cursor.getOpenContainerCursor();
            } else {
                imageCursor = cursor.getClosedContainerCursor();
            }
            setCursor(imageCursor);
        } else if (item instanceof Equipment) {
            ImageCursor cursor = gameController.getCursor().getMain();
            setCursor(cursor);
        } else {
            ImageCursor cursor = gameController.getCursor().getMain();
            setCursor(cursor);
        }
    }

    private Coords getMapCoords(double x, double y, double left, double top) {
        Coords pos = getMousePos(x, y, left, top);
        TEMP.x = pos.x;
        TEMP.y = pos.y;
        TEMP.add(curPos);
        return TEMP;
    }

    private void setCursor(ImageCursor main) {
        getCanvas().getScene().setCursor(main);
    }

    private void scrollDown(double locHeight) {
        double newY = curPos.y + gameController.getSettings().getGameScrollSpeed();
        curPos.y = Math.min(newY, locHeight - canvas.getHeight()/Sizes.getMeter());
    }

    private void scrollUp() {
        double newY = curPos.y - gameController.getSettings().getGameScrollSpeed();
        curPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        Settings settings = gameController.getSettings();
        int meter = Sizes.getMeter();
        double canvasWidth = canvas.getWidth();
        if (settings.isShowBar()) {
            double additionalWidth = barView.getWidth() / meter;
            locWidth += additionalWidth;
        }
        double newX = curPos.x + settings.getGameScrollSpeed();
        curPos.x = Math.min(newX, locWidth - canvasWidth/meter);
    }

    private void scrollLeft() {
        double newX = curPos.x - gameController.getSettings().getGameScrollSpeed();
        curPos.x = Math.max(newX, 0);
    }

    private void drawCreatureBase(Creature cr) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.getCenter();
        Coords translatedPos = translateCoordsToScreenCoords(centerBottomPos);
        drawCreatureBase(translatedPos.x, translatedPos.y, size, control);
    }

    private void hookUpEvents() {
        canvas.setFocusTraversable(true);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.MIDDLE)) {
                e.consume();
                isSelectionMode = true;
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.MIDDLE)) {
                e.consume();
                isSelectionMode = false;
                boolean multiple = e.isShiftDown();
                double left = selFirst.x;
                double top = selFirst.y;
                double right = selSecond.x;
                double bottom = selSecond.y;
                GameRunner.runLater(() -> resolveSelection(left, top, right, bottom, multiple));
                selFirst.x = -1;
                selFirst.y = -1;
                selSecond.x = -1;
                selSecond.y = -1;
            }
        });

        controller.getModel().getCurrentLocation().locationProperty().addListener(observable -> {
            layers = getSortedLayers();
        });

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
            controller.reloadInventoryPictures();
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            controller.reloadInventoryPictures();
        });
    }

    private void resolveSelection(double left, double top, double right, double bottom, boolean multiple) {
        Location location = controller.getCurrentLocation().getLocation();
        List<Creature> creatures = board.getControllablesWithinRectangle(left, top, right, bottom, location);
        creatures.forEach(c -> controller.getCreaturesToControl().add(c));
        if (!creatures.isEmpty() && !multiple) {
            board.looseCreaturesControl(location);
        }
    }

    private void defineRemovableEvents() {
        clickEvent = this::mouseClick;
        keyboardEvent = this::keyClick;
    }

    private void mouseClick(MouseEvent e) {
        if (isNotTimeToLetClick()) return;

        MouseButton button = e.getButton();
        double x = e.getX();
        double y = e.getY();

        Location location = controller.getCurrentLocation().getLocation();
        TEMP.x = x / Sizes.getMeter();
        TEMP.y = y / Sizes.getMeter();
        TEMP.add(curPos);
        double finalX = TEMP.x;
        double finalY = TEMP.y;

        if (button.equals(MouseButton.PRIMARY)) {
            if (gameController.getSettings().isShowBar()) {
                double barLeft = barView.getLeft();
                if (x > barLeft) {
                    return;
                }
            }
            e.consume();
            boolean multiple = e.isShiftDown();
            GameRunner.runLater(() -> onMapPrimaryButtonClick(location, finalX, finalY, multiple));
        } else if (button.equals(MouseButton.SECONDARY)) {
            e.consume();
            GameRunner.runLater(() -> onMapSecondaryButtonClick(location, finalX, finalY));
        }
    }

    private boolean isNotTimeToLetClick() {
        long curTime = System.currentTimeMillis();
        if (curTime < nextAvailableClickTime) {
            return true;
        }
        nextAvailableClickTime = curTime + Sizes.DIF_TIME_BETWEEN_CLICKS;
        return false;
    }

    private void keyClick(KeyEvent e) {
        if (isNotTimeToLetClick()) return;

        KeyCode key = e.getCode();
        List<KeyCode> barKeys = List.of(DIGIT1, DIGIT2, DIGIT3, DIGIT4, DIGIT5, DIGIT6);
        if (barKeys.contains(key)) {
            return;
        }
        switch (key) {
            case P -> {
                e.consume();
                Settings settings = gameController.getSettings();
                boolean isShowBar = settings.isShowBar();
                settings.setShowBar(!isShowBar);
                if (isShowBar) {
                    updateCurPosForShowBarUpdate();
                }
            }
            case I -> {
                e.consume();
                GameRunner.runLater(this::openInventory);
            }
            case SPACE -> {
                e.consume();
                handlePause();
            }
            case PAGE_UP -> {
                e.consume();
                GameRunner.runLater(() -> {
                    Layer layer = controller.getCurrentLayer().getLayer();
                    Layer next = layer;
                    for (int i = 0; i < layers.size() - 1; i++) {
                        Layer current = layers.get(i);
                        if (current == layer) {
                            next = layers.get(i + 1);
                        }
                    }
                    controller.getCurrentLayer().setLayer(next);
                });
            }
            case PAGE_DOWN -> {
                e.consume();
                GameRunner.runLater(() -> {
                    Layer layer = controller.getCurrentLayer().getLayer();
                    Layer prev = layer;
                    for (int i = 1; i < layers.size(); i++) {
                        Layer current = layers.get(i);
                        if (current == layer) {
                            prev = layers.get(i - 1);
                        }
                    }
                    controller.getCurrentLayer().setLayer(prev);
                });
            }
        }
    }

    private void updateCurPosForShowBarUpdate() {
        double locWidth = controller.getCurrentLocation().getWidth();
        int meter = Sizes.getMeter();
        double canvasWidth = canvas.getWidth();
        curPos.x = Math.min(curPos.x, locWidth - canvasWidth/meter);
    }

    private void onMapSecondaryButtonClick(Location location, double x, double y) {
        int level = controller.getCurrentLayer().getLevel();
        PosItem pi = board.lookForItem(location, x, y, level, SECONDARY_TYPES, false);
        if (pi == null) {
            board.looseCreaturesControl(location);
        } else {
            commandControllablesSecondAction(location, pi);
        }
    }

    private void commandControllablesSecondAction(Location location, PosItem pi) {
        board.getControlledCreatures(location)
                .forEach(c -> c.onSecondAction(pi));
    }

    private void onMapPrimaryButtonClick(Location location, double x, double y, boolean multiple) {
        List<Creature> controlled = board.getControlledCreatures(location);
        PosItem pi;
        int level = controller.getCurrentLayer().getLevel();
        if (controlled.isEmpty()) {
            pi = board.lookForItem(location, x, y, level, CREATURE_TYPE, false);
        } else {
            pi = board.lookForItem(location, x, y, level, PRIMARY_TYPES, false);
        }
        if (pi == null) {
            commandControlledGoTo(x, y);
        } else {
            boolean creatureSelected = false;
            if (pi instanceof Creature) {
                Creature selected = (Creature) pi;
                creatureSelected = chooseCreatures(selected, multiple);
            }
            if (!creatureSelected) {
                commandControlledFirstAction(pi);
            }
        }
    }

    private void hookUpRemovableEvents() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private void removeRemovableEvents() {
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private boolean chooseCreatures(Creature cr, boolean multiple) {
        CreatureControl control = cr.getControl();
        if (control == CONTROLLABLE || control == CONTROL) {
            if (!multiple) {
                board.looseCreaturesControl(cr.getPos().getLocation());
            }
            controller.getCreaturesToControl().add(cr);
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
        List<Creature> controlled = board.getControlledCreatures(location);
        if (controlled.isEmpty()) {
            return;
        }
        Creature active = controlled.get(0);
        controller.setCreatureToOpenInventory(active);
        controller.setInventory(true);
    }

    private void commandControlledFirstAction(PosItem pi) {
        Location location = controller.getCurrentLocation().getLocation();
        board.getControlledCreatures(location)
                .forEach(c -> c.onFirstAction(pi));
    }

    private void commandControlledGoTo(double x, double y) {
        Location location = controller.getCurrentLocation().getLocation();
        board.getControlledCreatures(location)
                .forEach(c -> c.goTo(x, y));
    }

    private void setSize() {
        Scene scene = canvas.getScene();
        if (scene == null) {
            return;
        }
        int locWidth = (int) (controller.getCurrentLocation().getWidth() * Sizes.getMeter());
        int locHeight = (int) (controller.getCurrentLocation().getHeight() * Sizes.getMeter());
        Settings settings = gameController.getSettings();
        int resWidth = settings.getResolutionWidth();
        int resHeight = settings.getResolutionHeight();
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

    private void clearBackground() {
        gc.setFill(Color.BLACK);
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

    public BarView getBarView() {
        return barView;
    }
}
