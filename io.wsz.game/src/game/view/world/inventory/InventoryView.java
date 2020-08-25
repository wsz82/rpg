package game.view.world.inventory;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.Container;
import io.wsz.model.item.*;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryView {
    private static final double SCROLL_BUTTON_PART = 1.0/80;
    private static final double CREATURE_X_POS = 0;
    private static final double CREATURE_Y_POS = 0.1;
    private static final double CREATURE_WIDTH = 0.35;
    private static final double CREATURE_HEIGHT = 0.8;
    private static final double HOLD_X_POS = 0.3;
    private static final double HOLD_Y_POS = 0.6;
    private static final double HOLD_WIDTH = 0.6;
    private static final double HOLD_HEIGHT = 0.3;

    private final Canvas canvas;
    private final GameController gameController;
    private final Controller controller;
    private final GraphicsContext gc;
    private final Coords mousePos = new Coords();
    private final Coords draggedCoords = new Coords();
    private final List<InventoryViewElement> equipmentViews = new ArrayList<>(0);
    private final HoldViewElement holdView;
    private final DropViewElement dropView;
    private final ContainerViewElement containerView;
    private final CreatureViewElement creatureView;

    private double draggedInitWidth;
    private double draggedInitHeight;
    private Equipment dragged;
    private EventHandler<MouseEvent> onClick;
    private EventHandler<MouseEvent> dragStop;
    private EventHandler<KeyEvent> closeEvent;
    private EventHandler<ScrollEvent> wheelScroll;
    private InventoryViewElement origin;
    private EquipmentViewElement scrolledVer;
    private DropViewElement scrolledHor;
    private InventoryViewElement lastCheckedView;

    public InventoryView(Canvas canvas, GameController gameController) {
        this.canvas = canvas;
        this.gameController = gameController;
        controller = gameController.getController();
        gc = canvas.getGraphicsContext2D();

        holdView = new HoldViewElement(canvas, gameController);
        equipmentViews.add(holdView);
        dropView = new DropViewElement(canvas, gameController);
        equipmentViews.add(dropView);
        containerView = new ContainerViewElement(canvas, gameController);
        equipmentViews.add(containerView);
        creatureView = new CreatureViewElement(canvas, gameController);
        equipmentViews.add(creatureView);

        hookupEvents();
    }

    private void hookupEvents() {
        closeEvent = e -> {
            KeyCode code = e.getCode();
            if (code.equals(KeyCode.I) || code.equals(KeyCode.ESCAPE)) {
                e.consume();
                synchronized (gameController.getGameRunner()) {
                    closeInventory();
                }
            }
        };
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, closeEvent);

        onClick = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (gameController.getGameRunner()) {
                    startDrag(mousePos.x, mousePos.y);
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (gameController.getGameRunner()) {
                    openContainer();
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, onClick);

        dragStop = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                scrolledVer = null;
                scrolledHor = null;
                if (dragged != null) {
                    e.consume();
                    mousePos.x = e.getX() / Sizes.getMeter();
                    mousePos.y = e.getY() / Sizes.getMeter();
                    synchronized (gameController.getGameRunner()) {
                        stopDrag(mousePos.x, mousePos.y);
                    }
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);

        wheelScroll = e -> {
            e.consume();
            InventoryViewElement ev = getView(mousePos.x, mousePos.y);
            if (ev == null) {
                return;
            }
            double dY = e.getDeltaY();
            if (dY < 0) {
                ev.scrollDown();
            } else {
                ev.scrollUp();
            }
        };
        canvas.addEventHandler(ScrollEvent.SCROLL, wheelScroll);
    }

    private void openContainer() {
        InventoryViewElement ev = getView(mousePos.x, mousePos.y);
        if (ev == null) return;
        if (ev instanceof ContainerViewElement) return;
        origin = ev;
        Coords translated = ev.getLocalCoords(mousePos);
        Equipment eq = ev.lookForEquipment(translated.x, translated.y, draggedCoords);
        if (!(eq instanceof Container)) return;
        Container toOpen = (Container) eq;
        Container openCon = controller.getContainerToOpen();
        if (toOpen == openCon) {
            openCon.close();
            controller.setContainerToOpen(null);
        } else {
            Creature cr = controller.getCreatureToOpenInventory();
            toOpen.searchContainer(cr);
        }
    }

    private void startDrag(double mouseX, double mouseY) {
        InventoryViewElement viewElement = getView(mouseX, mouseY);
        if (viewElement == null) return;
        origin = viewElement;
        Coords translated = viewElement.getLocalCoords(mousePos);
        Equipment selected = viewElement.lookForEquipment(translated.x, translated.y, draggedCoords);
        if (selected == null) return;
        Container toOpen = controller.getContainerToOpen();
        if (selected == toOpen) return;
        Creature creature = controller.getCreatureToOpenInventory();
        boolean canEquipmentBeRemoved = viewElement.tryRemove(selected, creature);
        if (canEquipmentBeRemoved) {
            dragged = selected.cloneEquipment();
            draggedInitWidth = dragged.getImageWidth();
            draggedInitHeight = dragged.getImageHeight();
        }
    }

    private void stopDrag(double mouseX, double mouseY) {
        if (dragged == null) return;
        dragged.getAnimationPos().setNextFrameUpdate(0);
        Creature hoveredHero = gameController.getHoveredHero();
        boolean isNotMovedToHero = !tryMoveToHero(hoveredHero);
        if (isNotMovedToHero) {
            moveEquipmentWithinInventoryElements(mouseX, mouseY);
        }
        dragged = null;
    }

    private boolean tryMoveToHero(Creature hero) {
        if (hero == null) return false;
        Creature cr = controller.getCreatureToOpenInventory();
        CreatureSize size = cr.getSize();
        if (hero.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (hero.getInventory().tryAdd(dragged)) {
                System.out.println(dragged.getName() + " moved to " + hero.getName() + " inventory");
                return true;
            }
        } else {
            System.out.println(hero.getName() + " out of " + cr.getName() + " range");
        }
        return false;
    }

    private void moveEquipmentWithinInventoryElements(double mouseX, double mouseY) {
        InventoryViewElement element = getView(mouseX, mouseY);
        Creature cr = controller.getCreatureToOpenInventory();
        if (element == null) {
            moveDraggedBack(cr);
        } else {
            Coords fixed = element.getFixedDraggedPos(mousePos, draggedCoords, dragged, draggedInitWidth, draggedInitHeight);
            boolean draggedEquipmentCannotBeAdded = !element.tryAdd(dragged, cr, fixed.x, fixed.y);
            if (draggedEquipmentCannotBeAdded) {
                moveDraggedBack(cr);
            }
        }
    }

    private void moveDraggedBack(Creature cr) {
        Coords extreme = origin.getExtremePos(mousePos, draggedCoords, dragged);
        if (extreme == null) {
            holdView.tryAdd(dragged, cr, 0, 0);
            return;
        }
        origin.tryAdd(dragged, cr, extreme.x, extreme.y);
    }

    private InventoryViewElement getView(double x, double y) {
        scrolledVer = null;
        scrolledHor = null;
        for (InventoryViewElement element : equipmentViews) {

            double evLeft = element.getViewPos().x;
            double evRight = evLeft + element.getViewWidth();
            double evTop = element.getViewPos().y;
            double evBottom = evTop + element.getViewHeight();

            if (x > evLeft && x < evRight
                    && y > evTop && y < evBottom) {
                return element;
            }

            if (element instanceof EquipmentViewElement) {
                EquipmentViewElement equipmentView = (EquipmentViewElement) element;
                double scrollWidth = equipmentView.getScrollWidth();
                if (equipmentView.isYScrollVisible()) {
                    double yScrollPos = equipmentView.getYScrollPos();
                    double yScrollButtonHeight = equipmentView.getYScrollButtonHeight();
                    if (x > evRight && x < evRight + scrollWidth
                            && y > evTop + yScrollPos && y < evTop + yScrollPos + yScrollButtonHeight) {
                        scrolledVer = equipmentView;
                        return null;
                    }
                    if (x > evRight && x < evRight + scrollWidth
                            && y > evTop && y < evBottom) {
                        equipmentView.setCurPosY(y - evTop);
                    }
                }
                if (element instanceof DropViewElement) {
                    DropViewElement dv = (DropViewElement) element;
                    if (dv.isXScrollVisible()) {
                        double xScrollPos = dv.getXScrollPos();
                        double xScrollButtonHeight = dv.getXScrollButtonWidth();
                        if (x > evLeft + xScrollPos && x < evLeft + xScrollPos + xScrollButtonHeight
                                && y > evBottom && y < evBottom + scrollWidth) {
                            scrolledHor = dv;
                            return null;
                        }
                        if (x > evLeft && x < evRight
                                && y > evBottom && y < evBottom + scrollWidth) {
                            dv.setScrollPosX(x - evLeft);
                        }
                    }
                }
            } else {

            }

        }
        return null;
    }

    private void closeInventory() {
        if (dragged != null) {
            Creature cr = controller.getCreatureToOpenInventory();
            holdView.tryAdd(dragged, cr, 0, 0);
            dragged = null;
        }
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScroll);
        controller.closeInventory();
    }

    public void refresh() {
        double width = canvas.getWidth();
        if (width == 0) return;
        double barViewWidth = Settings.getBarPart() * width;
        double inventoryWidth = (width - barViewWidth) / Sizes.getMeter();

        Creature cr = controller.getCreatureToOpenInventory();

        drawBackground(inventoryWidth);

        drawCreature(inventoryWidth, cr);

        drawHold(inventoryWidth, cr);

        drawDrop(inventoryWidth, cr);

        drawContainer(inventoryWidth);

        checkPos();
    }

    private void checkPos() {
        Bounds b = canvas.localToScreen(canvas.getBoundsInLocal());
        if (b == null) return;
        double left = b.getMinX();
        double top = b.getMinY();
        double right = b.getMaxX();
        double bottom = b.getMaxY();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < left
                || x > right
                || y < top
                || y > bottom) return;

        Coords mousePos = getMousePos(x, y, left, top);
        int meter = Sizes.getMeter();
        if (dragged != null) {
            playDraggedAnimation(mousePos);

            Image img = dragged.getImage();
            gc.drawImage(img,
                    mousePos.x*meter - img.getWidth()/2,
                    mousePos.y*meter - img.getHeight());
        }

        if (scrolledVer != null) {
            double scrolledPosY = scrolledVer.getViewPos().y;
            double mousePosY = mousePos.y;
            double scrollToY = mousePosY - scrolledPosY;
            scrolledVer.setCurPosY(scrollToY);
        }

        if (scrolledHor != null) {
            double scrolledPosX = scrolledHor.getViewPos().x;
            double mousePosX = mousePos.x;
            double scrollToX = mousePosX - scrolledPosX;
            scrolledHor.setScrollPosX(scrollToX);
        }
    }

    private void playDraggedAnimation(Coords mousePos) {
        InventoryViewElement ev = getView(mousePos.x, mousePos.y);
        EquipmentAnimationPos animationPos = dragged.getAnimationPos();
        if (ev instanceof DropViewElement) {
            if (!(lastCheckedView instanceof DropViewElement)) {
                animationPos.setNextFrameUpdate(0);
            }
            animationPos.setCurAnimation(EquipmentAnimationType.DROP);
        } else {
            if (lastCheckedView instanceof DropViewElement) {
                animationPos.setNextFrameUpdate(0);
            }
            animationPos.setCurAnimation(EquipmentAnimationType.INVENTORY);
        }
        lastCheckedView = ev;
        dragged.getAnimation().play(dragged);
    }

    private Coords getMousePos(double mouseX, double mouseY, double left, double top) {
        int meter = Sizes.getMeter();
        mousePos.x = (mouseX - left) / meter;
        mousePos.y = (mouseY - top) / meter;
        return mousePos;
    }

    private void drawHold(double inventoryWidth, Creature cr) {
        Inventory inventory = cr.getInventory();

        int meter = Sizes.getMeter();
        double canvasHeight = canvas.getHeight();
        double holdWidth = HOLD_WIDTH * inventoryWidth;
        double holdHeight = HOLD_HEIGHT * canvasHeight / meter;
        double x = HOLD_X_POS * inventoryWidth;
        double y = HOLD_Y_POS * canvasHeight / meter;

        holdView.setInventoryWidth(inventoryWidth);
        holdView.setDragged(dragged);
        holdView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / meter);
        holdView.setViewPos(x, y);
        holdView.setSize(holdWidth, holdHeight);
        holdView.setInventory(inventory);
        holdView.refresh();
    }

    private void drawDrop(double inventoryWidth, Creature cr) {
        List<Equipment> equipmentWithinRange;
        synchronized (gameController.getGameRunner()) {
            equipmentWithinRange = cr.getEquipmentWithinRange(controller);
        }

        int meter = Sizes.getMeter();
        double canvasHeight = canvas.getHeight();
        double scrollWidth = canvas.getWidth() * SCROLL_BUTTON_PART / meter;
        double x = 0.6 * inventoryWidth + scrollWidth;
        double y = 0.2 * canvasHeight / meter;
        double range = cr.getRange();

        double height = cr.getSize().getHeight() + 2*range;
        dropView.setVisionHeightDiameter(height);
        double width = cr.getSize().getWidth() + 2*range;
        dropView.setVisionWidthDiameter(width);

        double maxDropHeight = 0.3 * canvasHeight / meter;
        double maxDropWidth = 0.3 * inventoryWidth;

        double resultHeight = height;
        double resultWidth = width;

        double ratio = height/width;

        if (height >= maxDropHeight) {
            resultHeight = maxDropHeight;
            resultWidth = width / ratio;
            if (resultWidth >= maxDropWidth) {
                resultWidth = maxDropWidth;
                resultHeight = resultHeight * ratio;
            }
        } else if (width >= maxDropWidth) {
            resultWidth = maxDropWidth;
            resultHeight = height * ratio;
            if (resultHeight >= maxDropHeight) {
                resultHeight = maxDropHeight;
                resultWidth = resultWidth / ratio;
            }
        }

        dropView.setScrollWidth(scrollWidth);
        dropView.setViewPos(x, y);
        dropView.setSize(resultWidth, resultHeight);
        Coords center = cr.getCenter();
        dropView.setCreaturePos(center.x, center.y);
        dropView.setDroppedEquipment(equipmentWithinRange);
        dropView.refresh();
    }

    private void drawContainer(double inventoryWidth) {
        Creature cr = controller.getCreatureToOpenInventory();
        Container con = controller.getContainerToOpen();
        if (con == null) return;
        CreatureSize size = cr.getSize();
        boolean inventoryNotContainContainerToOpen = !holdView.getItems().contains(con);
        if (inventoryNotContainContainerToOpen) {
            boolean containerOutOfRange = !con.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight());
            if (containerOutOfRange) return;
        }
        int meter = Sizes.getMeter();
        double width = 0.3 * inventoryWidth;
        double height = 0.3 * canvas.getHeight() / meter;
        double x = 0.3 * inventoryWidth;
        double y = 0.2 * canvas.getHeight() / meter;

        containerView.setInventoryWidth(inventoryWidth);
        containerView.setDragged(dragged);
        containerView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / meter);
        containerView.setViewPos(x, y);
        containerView.setSize(width, height);
        containerView.setContainer(con);
        containerView.refresh();
    }

    private void drawCreature(double inventoryWidth, Creature cr) {
        int meter = Sizes.getMeter();
        double creatureViewWidth = CREATURE_WIDTH * inventoryWidth;
        double creatureViewHeight = CREATURE_HEIGHT * canvas.getHeight() / meter;
        double x = CREATURE_X_POS * inventoryWidth;
        double y = CREATURE_Y_POS * canvas.getHeight() / meter;

        creatureView.setDrawnCreature(cr);
        creatureView.setDragged(dragged);
        creatureView.setViewPos(x, y);
        creatureView.setSize(creatureViewWidth, creatureViewHeight);
        creatureView.refresh();
    }

    private void drawBackground(double inventoryWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, inventoryWidth * Sizes.getMeter(), canvas.getHeight());
    }
}
