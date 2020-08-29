package game.view.world.board;

import game.model.GameController;
import game.model.setting.Settings;
import game.model.world.GameRunner;
import game.view.world.CanvasView;
import game.view.world.FoggableDelegate;
import game.view.world.dialog.DialogView;
import game.view.world.inventory.InventoryView;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
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
import static io.wsz.model.item.ItemType.*;
import static javafx.scene.input.KeyCode.*;

public class GameView extends CanvasView {
    private static final double OFFSET = 0.3 * Sizes.getMeter();
    private static final ItemType[] PRIMARY_TYPES =
            new ItemType[] {CREATURE, CONTAINER, WEAPON, INDOOR, OUTDOOR};
    private static final ItemType[] SECONDARY_TYPES =
            new ItemType[] {INDOOR, OUTDOOR, CONTAINER};
    private static final ItemType[] CREATURE_TYPE = new ItemType[] {CREATURE};

    private final Stage parent;
    private final List<PosItem> items = new ArrayList<>(0);
    private final Coords mousePos = new Coords();
    private final Coords modifiedCoords = new Coords();
    private final Coords selFirst = new Coords(-1, -1);
    private final Coords selSecond = new Coords(-1, -1);
    private final BarView barView;
    private final Coords curPos;
    private final FoggableDelegate foggableDelegate;

    private List<Layer> layers;
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
        if (Settings.isShowBar()) {
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
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(curPos);
        return modifiedCoords;
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
        if (posToCenter.x != -1) {
            board.centerScreenOn(posToCenter, canvas.getWidth() / meter, canvas.getHeight() / meter);
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

        if (isSelectionMode) {
            Coords pos = getMousePos(x, y, left, top);
            modifiedCoords.x = pos.x;
            modifiedCoords.y = pos.y;
            modifiedCoords.add(curPos);

            if (selFirst.x == -1) {
                selFirst.x = modifiedCoords.x;
                selFirst.y = modifiedCoords.y;
            } else {
                selSecond.x = modifiedCoords.x;
                selSecond.y = modifiedCoords.y;
            }
        }

        if (Settings.isCenterOnPC()) {
            Location location = controller.getCurrentLocation().getLocation();
            List<Creature> controlledCreatures = board.getControlledCreatures(location);
            if (!controlledCreatures.isEmpty()) {
                Creature cr = controlledCreatures.get(0);
                if (cr != null) {
                    board.centerScreenOn(cr.getCenter(), canvas.getWidth() / meter, canvas.getHeight() / meter);
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
        double newY = curPos.y + Settings.getGameScrollSpeed();
        curPos.y = Math.min(newY, locHeight - canvas.getHeight()/Sizes.getMeter());
    }

    private void scrollUp() {
        double newY = curPos.y - Settings.getGameScrollSpeed();
        curPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        double newX = curPos.x + Settings.getGameScrollSpeed();
        curPos.x = Math.min(newX, locWidth - canvas.getWidth()/Sizes.getMeter());
    }

    private void scrollLeft() {
        double newX = curPos.x - Settings.getGameScrollSpeed();
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
            controller.clearResizablePictures();
        });
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
            controller.clearResizablePictures();
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
        clickEvent = e -> {
            MouseButton button = e.getButton();
            double x = e.getX();
            double y = e.getY();

            Location location = controller.getCurrentLocation().getLocation();
            modifiedCoords.x = x / Sizes.getMeter();
            modifiedCoords.y = y / Sizes.getMeter();
            modifiedCoords.add(curPos);
            double finalX = modifiedCoords.x;
            double finalY = modifiedCoords.y;

            if (button.equals(MouseButton.PRIMARY)) {
                if (Settings.isShowBar()) {
                    double barLeft = barView.getLeft();
                    if (x > barLeft) {
                        return;
                    }
                }
                e.consume();
                boolean multiple = e.isShiftDown();
                GameRunner.runLater(() -> {
                    onMapPrimaryButtonClick(location, finalX, finalY, multiple);
                });
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                GameRunner.runLater(() -> onMapSecondaryButtonClick(location, finalX, finalY));
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
        };
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
}
