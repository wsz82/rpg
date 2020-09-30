package game.view.world.board;

import game.model.GameController;
import game.model.setting.KeyAction;
import game.model.setting.Settings;
import game.model.textures.Cursor;
import game.model.world.GameRunner;
import game.view.world.CanvasView;
import game.view.world.FoggableDelegate;
import game.view.world.dialog.DialogView;
import game.view.world.inventory.InventoryView;
import io.wsz.model.animation.creature.CreatureBaseAnimationType;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentObservableLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
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
    private static final ItemType[] SECONDARY_TYPES =
            new ItemType[] {INDOOR, OUTDOOR, CONTAINER};
    private static final ItemType[] CREATURE_TYPE = new ItemType[] {CREATURE};

    private final Stage parent;
    private final Coords temp = new Coords();
    private final Coords selFirst = new Coords(-1, -1);
    private final Coords selSecond = new Coords(-1, -1);
    private final InventoryView inventoryView;
    private final DialogView dialogView;
    private final BarView barView;
    private final Coords curPos;
    private final FoggableDelegate foggableDelegate;

    private List<Layer> layers;
    private long nextAvailableClickTime;
    private EventHandler<MouseEvent> clickEvent;
    private EventHandler<KeyEvent> keyboardEvent;
    private boolean canStartDialog = true;
    private boolean canOpenInventory = true;
    private boolean isSelectionMode;

    public GameView(Stage parent, GameController gameController) {
        super(new Canvas(), gameController);
        this.parent = parent;
        inventoryView = new InventoryView(canvas, gameController);
        dialogView = new DialogView(canvas, gameController, OFFSET);
        barView = new BarView(canvas, gameController);
        mousePos = new Coords();
        curPos = controller.getCurPos();
        foggableDelegate = new FoggableDelegate(gameController, canvas, curPos);
        hookUpEvents();
        defineRemovableEvents();
        hookUpRemovableEvents();
    }

    public void refresh(List<PosItem> sortedItems) {
        if (parent.isIconified()) {
            return;
        }
        setSize();
        if (canvas.getWidth() == 0) {
            return;
        }

        if (tryStartDialog()) return;
        if (tryOpenInventory()) return;

        forceRefresh(sortedItems);
        barView.refresh();
    }

    public void forceRefresh(List<PosItem> sortedItems) {
        updatePos();
        clearBackground();

        Location location = controller.getCurrentLocation().getLocation();
        List<Creature> heroes = board.getControlledAndControllableCreatures(location);

        drawItems(sortedItems, heroes);

        drawFog();

        if (isCursorOnCountable) {
            drawCountableText();
        }

        if (isSelectionMode) {
            drawSelection();
        }
    }

    private void drawFog() {
        int meter = Sizes.getMeter();
        double width = canvas.getWidth() / meter;
        double height = canvas.getHeight() / meter;
        foggableDelegate.drawFog(width, height);
    }

    private boolean tryOpenInventory() {
        if (controller.isInventory()) {
            if (canOpenInventory) {
                canOpenInventory = false;
                removeRemovableEvents();
                inventoryView.hookUpRemovableEvents();
            }
            return true;
        }
        if (!canOpenInventory) {
            canOpenInventory = true;
            hookUpRemovableEvents();
        }
        return false;
    }

    private boolean tryStartDialog() {
        if (controller.isDialog()) {
            if (canStartDialog) {
                canStartDialog = false;
                removeRemovableEvents();
                dialogView.hookUpRemovableEvents();
                dialogView.setIsToRefresh(true);
                dialogView.setGameViewNotRefreshedOnce(true);
                }
                return true;
        }
        if (!canStartDialog) {
            canStartDialog = true;
            hookUpRemovableEvents();
        }
        return false;
    }

    private void drawItems(List<PosItem> sortedItems, List<Creature> heroes) {
        for (PosItem pi : sortedItems) {
            if (heroes != null) {
                adjustCoverOpacity(heroes, pi);
            }

            Coords pos = pi.getPos();
            Coords translatedPos = translateCoordsToScreenCoords(pos);
            int meter = Sizes.getMeter();
            double x = translatedPos.x * meter;
            double y = translatedPos.y * meter;

            if (pi instanceof Creature) { //TODO generic
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
        temp.x = pos.x;
        temp.y = pos.y;
        temp.subtract(curPos);
        return temp;
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
        Settings settings = controller.getSettings();
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
        CurrentObservableLocation currentObservableLocation = controller.getCurrentLocation();
        double locWidth = currentObservableLocation.getWidth();
        double locHeight = currentObservableLocation.getHeight();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < left || x > right || y < top || y > bottom) {
            controller.getCursor().setCursor(CursorType.MAIN);
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

        Location location = currentObservableLocation.getLocation();

        if (settings.isCenterOnPC()) {
            centerOnPC(meter, canvasMeterWidth, canvasMeterHeight, settings, location);
        }

        List<Creature> controlledCreatures = board.getControlledCreatures(location);
        Creature selected = null;
        if (!controlledCreatures.isEmpty()) {
            selected = controlledCreatures.get(0); //TODO selected creatures stand order
        }
        setAppropriateCursor(selected, pos, 0, 0, locWidth, locHeight, location.getItems());

        if (x >= left+OFFSET && x <= right-OFFSET
                && y >= top+OFFSET && y <= bottom-OFFSET) {
            return;
        }

        Cursor cursor = controller.getCursor();
        if (x < left+OFFSET && x >= left
                && y > top+OFFSET && y < bottom-OFFSET) {
            cursor.setCursor(CursorType.LEFT);
            scrollLeft();
        } else
        if (x > right-OFFSET && x <= right
                && y > top+OFFSET && y < bottom-OFFSET) {
            cursor.setCursor(CursorType.RIGHT);
            scrollRight(locWidth);
        } else
        if (y < top+OFFSET && y >= top
                && x > left+OFFSET && x < right-OFFSET) {
            cursor.setCursor(CursorType.UP);
            scrollUp();
        } else
        if (y > bottom-OFFSET && y <= bottom
                && x > left+OFFSET && x < right-OFFSET) {
            cursor.setCursor(CursorType.DOWN);
            scrollDown(locHeight);
        } else
        if (x < left+OFFSET && x >= left
                && y >= top && y < top+OFFSET) {
            cursor.setCursor(CursorType.LEFT_UP);
            scrollLeft();
            scrollUp();
        } else
        if (x > right-OFFSET && x <= right
                && y >= top && y < top+OFFSET) {
            cursor.setCursor(CursorType.RIGHT_UP);
            scrollRight(locWidth);
            scrollUp();
        } else
        if (x < left+OFFSET && x >= left
                && y >= bottom-OFFSET && y < bottom) {
            cursor.setCursor(CursorType.LEFT_DOWN);
            scrollLeft();
            scrollDown(locHeight);
        } else
        if (x > right-OFFSET && x <= right
                && y >= bottom-OFFSET && y < bottom) {
            cursor.setCursor(CursorType.RIGHT_DOWN);
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

    private Coords getMapCoords(double x, double y, double left, double top) {
        Coords pos = getMousePos(x, y, left, top);
        temp.x = pos.x;
        temp.y = pos.y;
        temp.level = controller.getCurrentLayer().getLevel();
        temp.add(curPos);
        return temp;
    }

    private void scrollDown(double locHeight) {
        double newY = curPos.y + controller.getSettings().getGameScrollSpeed();
        curPos.y = Math.min(newY, locHeight - canvas.getHeight()/Sizes.getMeter());
    }

    private void scrollUp() {
        double newY = curPos.y - controller.getSettings().getGameScrollSpeed();
        curPos.y = Math.max(newY, 0);
    }

    private void scrollRight(double locWidth) {
        Settings settings = controller.getSettings();
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
        double newX = curPos.x - controller.getSettings().getGameScrollSpeed();
        curPos.x = Math.max(newX, 0);
    }

    private void drawCreatureBase(Creature cr) {
        CreatureControl control = cr.getControl();
        boolean isCreatureControllableOrNeutral = control == CONTROLLABLE || control == CreatureControl.NEUTRAL;
        if (isCreatureControllableOrNeutral && isCreatureBaseNotOnAction(cr)) {
            return;
        }
        Coords centerBottomPos = cr.getCenter();
        Coords translatedPos = translateCoordsToScreenCoords(centerBottomPos);
        ResolutionImage base = cr.getBase();
        drawCreatureBase(translatedPos.x, translatedPos.y, base);
    }

    private boolean isCreatureBaseNotOnAction(Creature cr) {
        CreatureBaseAnimationType curAnimation = cr.getBaseAnimationPos().getBaseAnimationType();
        return curAnimation != CreatureBaseAnimationType.ACTION;
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
        temp.x = x / Sizes.getMeter();
        temp.y = y / Sizes.getMeter();
        temp.add(curPos);
        double finalX = temp.x;
        double finalY = temp.y;

        if (button.equals(MouseButton.PRIMARY)) {
            if (controller.getSettings().isShowBar()) {
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
        KeyCode pause = controller.getSettings().getKey(KeyAction.PAUSE);
        KeyCode inventory = controller.getSettings().getKey(KeyAction.INVENTORY);
        KeyCode hideOrShowPortraits = controller.getSettings().getKey(KeyAction.HIDE_PORTRAITS);
        KeyCode layerUp = controller.getSettings().getKey(KeyAction.LAYER_UP);
        KeyCode layerDown = controller.getSettings().getKey(KeyAction.LAYER_DOWN);
        if (key == ESCAPE) {
            ImageCursor main = controller.getCursor().getMain();
            controller.setCursor(main);
        } else if (key == hideOrShowPortraits) {
            e.consume();
            switchPortraits();
        } else if (key == inventory) {
            e.consume();
            GameRunner.runLater(this::openInventory);
        } else if (key == pause) {
            e.consume();
            handlePause();
        } else if (key == layerUp) {
            e.consume();
            GameRunner.runLater(this::changeToLayerUp);
        } else if (key == layerDown) {
            e.consume();
            GameRunner.runLater(this::changeToLayerDown);
        }
    }

    private void switchPortraits() {
        Settings settings = controller.getSettings();
        boolean isShowBar = settings.isShowBar();
        settings.setShowBar(!isShowBar);
        if (isShowBar) {
            updateCurPosForShowBarUpdate();
        }
    }

    private void changeToLayerDown() {
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

    private void changeToLayerUp() {
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

    private void updateCurPosForShowBarUpdate() {
        double locWidth = controller.getCurrentLocation().getWidth();
        int meter = Sizes.getMeter();
        double canvasWidth = canvas.getWidth();
        curPos.x = Math.min(curPos.x, locWidth - canvasWidth/meter);
    }

    private void onMapSecondaryButtonClick(Location location, double x, double y) {
        int level = controller.getCurrentLayer().getLevel();
        PosItem pi = board.lookForItem(location.getItems(), x, y, level, SECONDARY_TYPES, false);
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
            pi = board.lookForItem(location.getItems(), x, y, level, CREATURE_TYPE, false);
        } else {
            pi = board.lookForItem(location.getItems(), x, y, level, CURSOR_TYPES, false);
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
        boolean isGame = controller.isGame();
        controller.setGame(!isGame);
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
        Settings settings = controller.getSettings();
        int resWidth = settings.getHorizontalResolution();
        int resHeight = settings.getVerticalResolution();
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

    public InventoryView getInventoryView() {
        return inventoryView;
    }

    public DialogView getDialogView() {
        return dialogView;
    }
}
