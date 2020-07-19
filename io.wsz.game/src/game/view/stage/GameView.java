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
import java.util.Objects;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;
import static io.wsz.model.item.CreatureControl.CONTROLLABLE;
import static io.wsz.model.item.ItemType.*;
import static javafx.scene.input.KeyCode.*;

public class GameView extends CanvasView {
    private static final double OFFSET = 0.3 * Sizes.getMeter();
    private static final Coords[] POSS = new Coords[]{new Coords()};
    private static final ItemType[] PRIMARY_ITEM_TYPES =
            new ItemType[] {CREATURE, CONTAINER, WEAPON, TELEPORT, INDOOR, OUTDOOR};
    private static final ItemType[] SECONDARY_ITEM_TYPES =
            new ItemType[] {INDOOR, OUTDOOR, CONTAINER};

    private final Stage parent;
    private final GameController gameController = GameController.get();
    private final List<PosItem> items = new ArrayList<>(0);
    private final Coords curPos = controller.getBoardPos();
    private final Coords mousePos = new Coords();
    private final Coords modifiedCoords1 = new Coords();
    private final Coords modifiedCoords2 = new Coords();
    private final Coords nextPiecePos = new Coords();
    private final Coords selFirst = new Coords(-1, -1);
    private final Coords selSecond = new Coords(-1, -1);
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
    private final Image fogPiece;

    public GameView(Stage parent) {
        super(new Canvas());
        this.parent = parent;
        fogPiece = getFogPiece();

        hookUpEvents();
        defineRemovableEvents();
        hookUpRemovableEvents();
    }

    private Image getFogPiece() {
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("fog.png")));
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

        Location location = Controller.get().getCurrentLocation().getLocation();
        List<Creature> heroes = board.getControlledAndControllableCreatures(location);

        drawItems(heroes);

        drawFog(heroes);

        if (selectionMode) {
            drawSelection();
        }

        if (Settings.isShowBar()) {
            barView.refresh();
        }
    }

    private void drawFog(List<Creature> heroes) {
        Location loc = controller.getCurrentLocation().getLocation();
        List<List<Boolean>> discoveredFog = loc.getDiscoveredFog();
        int maxPiecesHeight = (int) Math.ceil(5.0/4 * loc.getHeight() * 2) + 4;
        int maxPiecesWidth = (int) Math.ceil(loc.getWidth() * 2) + 1;
        if (discoveredFog == null) {
            discoveredFog = new ArrayList<>(maxPiecesHeight);
            for (int i = 0; i < maxPiecesHeight; i++) {
                ArrayList<Boolean> horList = new ArrayList<>(maxPiecesWidth);
                for (int j = 0; j < maxPiecesWidth; j++) {
                    horList.add(false);
                }
                discoveredFog.add(i, horList);
            }
            loc.setDiscoveredFog(discoveredFog);
        }

        gc.setImageSmoothing(false);
        double fogSize = Sizes.getFogSize();
        double offset = 3.0/4 * fogSize;

        double y = -1.0/4 * fogSize;
        double x = 0;

        for (int i = 0; i < maxPiecesHeight; i++) {
            if (i != 0) {
                y += offset;
            }
            for (int j = 0; j < maxPiecesWidth; j++) {
                if (j == 0) {
                    if (i % 2 == 0) {
                        x = -fogSize/2;
                    } else {
                        x = -fogSize;
                    }
                } else {
                    x += fogSize;
                }
                nextPiecePos.x = x;
                nextPiecePos.y = y;
                Coords translatedPiecePos = translateCoordsToScreenCoords1(nextPiecePos);
                boolean fogWithinVision = false;
                for (Creature cr : heroes) {
                    double visionRange = cr.getVisionRange();
                    Coords translatedPos = translateCoordsToScreenCoords2(cr.getCenter());
                    double visWidth = visionRange * 2;
                    double visHeight = visionRange * 2.0/3 * 2;
                    fogWithinVision = Coords.pointWithinOval(translatedPiecePos, translatedPos, visWidth, visHeight);
                    if (fogWithinVision) {
                        break;
                    }
                }
                if (fogWithinVision) {
                    discoveredFog.get(i).set(j, true);
                } else {
                    boolean pieceDiscovered = discoveredFog.get(i).get(j);
                    if (pieceDiscovered) {
                        gc.setGlobalAlpha(0.3);
                        drawFogPiece(translatedPiecePos);
                        gc.setGlobalAlpha(1);
                    } else {
                        drawFogPiece(translatedPiecePos);
                    }
                }
            }
        }
        gc.setImageSmoothing(true);
    }

    private void drawFogPiece(Coords pos) {
        pos.x *= Sizes.getMeter();
        pos.y *= Sizes.getMeter();
        gc.drawImage(fogPiece, pos.x, pos.y);
    }

    private void drawItems(List<Creature> heroes) {
        for (PosItem pi : items) {
            if (heroes != null) {
                adjustCoverOpacity(heroes, pi);
            }

            Coords pos = pi.getPos();
            Coords translatedPos = translateCoordsToScreenCoords1(pos);
            double x = translatedPos.x * Sizes.getMeter();
            double y = translatedPos.y * Sizes.getMeter();

            if (pi instanceof Creature) {
                drawCreatureSize((Creature) pi);
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
    }

    private void sortItems() {
        Location location = controller.getCurrentLocation().getLocation();
        int level = controller.getCurrentLayer().getLevel();
        double canvasWidth = canvas.getWidth() / Sizes.getMeter();
        double canvasHeight = canvas.getHeight() / Sizes.getMeter();
        sortItems(location, curPos, canvasWidth, canvasHeight,
                items, level);
    }

    private void drawSelection() {
        if (selFirst.x == -1 || selSecond.x == -1) {
            return;
        }
        Coords translatedPos = translateCoordsToScreenCoords1(selFirst);
        final int firstX = (int) (translatedPos.x * Sizes.getMeter());
        final int firstY = (int) (translatedPos.y * Sizes.getMeter());

        translatedPos = translateCoordsToScreenCoords1(selSecond);
        final int secondX = (int) (translatedPos.x * Sizes.getMeter());
        final int secondY = (int) (translatedPos.y * Sizes.getMeter());

        int x = Math.min(firstX, secondX);
        int y = Math.min(firstY, secondY);
        int width = Math.abs(firstX - secondX);
        int height = Math.abs(firstY - secondY);
        gc.setStroke(Color.DARKOLIVEGREEN);
        gc.strokeRect(x, y, width, height);
    }

    private Coords translateCoordsToScreenCoords1(Coords pos) {
        modifiedCoords1.x = pos.x;
        modifiedCoords1.y = pos.y;
        modifiedCoords1.subtract(curPos);
        return modifiedCoords1;
    }

    private Coords translateCoordsToScreenCoords2(Coords pos) {
        modifiedCoords2.x = pos.x;
        modifiedCoords2.y = pos.y;
        modifiedCoords2.subtract(curPos);
        return modifiedCoords2;
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
            modifiedCoords1.x = pos.x;
            modifiedCoords1.y = pos.y;
            modifiedCoords1.add(curPos);

            if (selFirst.x == -1) {
                selFirst.x = modifiedCoords1.x;
                selFirst.y = modifiedCoords1.y;
            } else {
                selSecond.x = modifiedCoords1.x;
                selSecond.y = modifiedCoords1.y;
            }
        }

        if (constantWalk) {
            Coords pos = getMousePos(x, y, left, top);
            modifiedCoords1.x = pos.x;
            modifiedCoords1.y = pos.y;
            modifiedCoords1.add(curPos);
            commandControllableGoTo(modifiedCoords1);
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

    private void drawCreatureSize(Creature cr) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.getCenter();
        Coords translatedPos = translateCoordsToScreenCoords1(centerBottomPos);
        double x = translatedPos.x * Sizes.getMeter();
        double y = translatedPos.y * Sizes.getMeter();
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
                    onMapSecondaryButtonClick(e.getX(), e.getY());
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

    private void onMapSecondaryButtonClick(double x, double y) {
        Location location = controller.getCurrentLocation().getLocation();
        modifiedCoords1.x = x / Sizes.getMeter();
        modifiedCoords1.y = y / Sizes.getMeter();
        modifiedCoords1.add(curPos);
        POSS[0].x = modifiedCoords1.x;
        POSS[0].y = modifiedCoords1.y;
        PosItem pi = board.lookForContent(location, POSS, SECONDARY_ITEM_TYPES, false);
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

    private void onMapPrimaryButtonClick(double x, double y, boolean multiple) {
        Location location = controller.getCurrentLocation().getLocation();
        modifiedCoords1.x = x / Sizes.getMeter();
        modifiedCoords1.y = y / Sizes.getMeter();
        modifiedCoords1.add(curPos);
        POSS[0].x = modifiedCoords1.x;
        POSS[0].y = modifiedCoords1.y;
        PosItem pi = board.lookForContent(location, POSS, PRIMARY_ITEM_TYPES, false);
        if (pi == null) {
            commandControllableGoTo(modifiedCoords1);
            constantWalk = true;
        } else {
            boolean creatureSelected = false;
            if (pi instanceof Creature) {
                Creature selected = (Creature) pi;
                creatureSelected = chooseCreatures(selected, multiple);
            }
            if (!creatureSelected) {
                commandControllableFirstAction(pi);
                constantWalk = true;
            }
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

    private boolean chooseCreatures(Creature cr, boolean multiple) {
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
            curPos.x = locWidth - canvasWidth;
        } else {
            curPos.x = Math.max(x, 0);
        }
        if (y > locHeight - canvasHeight) {
            curPos.y = locHeight - canvasHeight;
        } else {
            curPos.y = Math.max(y, 0);
        }
    }

    private void commandControllableFirstAction(PosItem pi) {
        Location location = controller.getCurrentLocation().getLocation();
        board.getControlledCreatures(location)
                .forEach(c -> c.onFirstAction(pi));
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

    public Coords getCurPos() {
        return curPos;
    }
}
