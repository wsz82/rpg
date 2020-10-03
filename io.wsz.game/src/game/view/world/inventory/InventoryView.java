package game.view.world.inventory;

import game.model.GameController;
import game.model.setting.KeyAction;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.item.Container;
import io.wsz.model.item.*;
import io.wsz.model.item.movement.InventoryCountableMover;
import io.wsz.model.item.movement.InventoryEquipmentMover;
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
    private final GameController controller;
    private final GraphicsContext gc;
    private final Coords mousePos = new Coords();
    private final Coords imgRelativeCoords = new Coords();
    private final Coords temp1 = new Coords();
    private final Coords temp2 = new Coords();
    private final List<InventoryViewElement> inventoryElements = new ArrayList<>(0);
    private final HoldViewElement holdView;
    private final DropViewElement dropView;
    private final ContainerViewElement containerView;
    private final CreatureViewElement creatureView;
    private final CountableRelocationWindow countableRelocationWindow;

    private double draggedInitWidth;
    private double draggedInitHeight;
    private EventHandler<MouseEvent> onClick;
    private EventHandler<MouseEvent> dragStop;
    private EventHandler<KeyEvent> closeEvent;
    private EventHandler<ScrollEvent> wheelScroll;
    private InventoryViewElement origin;
    private InventoryViewElement lastCheckedView;
    private InventoryViewElement dragScrolledView;

    public InventoryView(Canvas canvas, GameController controller) {
        this.canvas = canvas;
        this.controller = controller;
        gc = canvas.getGraphicsContext2D();

        holdView = new HoldViewElement(canvas, controller, mousePos);
        inventoryElements.add(holdView);
        dropView = new DropViewElement(canvas, controller, mousePos);
        inventoryElements.add(dropView);
        containerView = new ContainerViewElement(canvas, controller, mousePos);
        inventoryElements.add(containerView);
        creatureView = new CreatureViewElement(canvas, controller, mousePos);
        inventoryElements.add(creatureView);

        countableRelocationWindow = new CountableRelocationWindow(controller, canvas, gc, mousePos);

        defineRemovableEvents();
    }

    private void defineRemovableEvents() {
        closeEvent = e -> {
            KeyCode code = e.getCode();
            KeyCode inventoryClose = controller.getSettings().getKey(KeyAction.INVENTORY);
            if (code.equals(inventoryClose) || code.equals(KeyCode.ESCAPE)) {
                e.consume();
                synchronized (controller.getGameRunner()) {
                    closeInventory();
                }
            }
        };

        onClick = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (controller.getGameRunner()) {
                    startDragEquipment(mousePos.x, mousePos.y);
                    startDragScrollBar(mousePos.x, mousePos.y);
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (controller.getGameRunner()) {
                    openContainer();
                }
            }
        };

        dragStop = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                if (controller.getDragged() != null) {
                    e.consume();
                    mousePos.x = e.getX() / Sizes.getMeter();
                    mousePos.y = e.getY() / Sizes.getMeter();
                    double x = mousePos.x;
                    double y = mousePos.y;
                    synchronized (controller.getGameRunner()) {
                        stopDragEquipment(x, y);
                    }
                }
                if (dragScrolledView != null) {
                    stopScrollDrag();
                }
            }
        };

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
    }

    public void hookUpRemovableEvents() {
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.addEventHandler(ScrollEvent.SCROLL, wheelScroll);
    }

    private void removeRemovableEvents() {
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScroll);
    }

    public void refresh(List<PosItem<?,?>> sortedMapItems, List<Equipment<?,?>> sortedHoldItems, List<Equipment<?,?>> sortedContainerItems) {
        double width = canvas.getWidth();
        if (width == 0) return;
        double barViewWidth = controller.getGameView().getBarView().getWidth();
        double inventoryWidth = (width - barViewWidth) / Sizes.getMeter();

        Creature cr = controller.getCreatureToOpenInventory();

        drawBackground(inventoryWidth);

        drawCreature(inventoryWidth, cr);

        drawHold(sortedHoldItems, inventoryWidth, cr);

        drawDrop(sortedMapItems, inventoryWidth, cr);

        drawContainer(sortedContainerItems, inventoryWidth);

        drawCountableRelocationWindow(inventoryWidth);

        checkPos();
    }

    private void stopScrollDrag() {
        dragScrolledView.setIsScrollDragged(false);
        dragScrolledView = null;
    }

    private void openContainer() {
        InventoryViewElement ev = getView(mousePos.x, mousePos.y);
        if (ev == null) return;
        if (ev instanceof ContainerViewElement) return;
        origin = ev;
        Coords translated = ev.getLocalCoords();
        Equipment<?,?> eq = ev.lookForEquipment(translated.x, translated.y, imgRelativeCoords);
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

    private void startDragEquipment(double mouseX, double mouseY) {
        InventoryViewElement viewElement = getView(mouseX, mouseY);
        if (viewElement == null) return;
        origin = viewElement;
        Coords translated = viewElement.getLocalCoords();
        Equipment<?,?> selected = viewElement.lookForEquipment(translated.x, translated.y, imgRelativeCoords);
        if (selected == null) return;
        Container toOpen = controller.getContainerToOpen();
        if (selected == toOpen) return;
        Creature creature = controller.getCreatureToOpenInventory();
        boolean canEquipmentBeRemoved = viewElement.tryRemove(selected, creature);
        if (canEquipmentBeRemoved) {
            Equipment<?,?> dragged = selected.cloneEquipment(true);
            controller.setDragged(dragged);
            draggedInitWidth = dragged.getImageWidth();
            draggedInitHeight = dragged.getImageHeight();
        }
    }

    private void stopDragEquipment(double mouseX, double mouseY) {
        Equipment<?,?> dragged = controller.getDragged();
        if (dragged == null) return;
        dragged.getAnimationPos().setNextFrameUpdate(0);
        Creature hoveredHero = controller.getHoveredHero();
        Creature cr = controller.getCreatureToOpenInventory();


        boolean isMany = false;
        int amount = 1;
        boolean isCountable = dragged.isCountable();
        if (isCountable) {
            amount = dragged.getAmount();
            isMany = amount != 1;
        }

        if (isMany) {
            int finalAmount = amount;
            InventoryCountableMover countableMover = c -> {
                controller.setDragged(null);
                moveCountableEquipment(mouseX, mouseY, hoveredHero, cr, c, finalAmount);
            };
            dragged.moveEquipment(null, countableMover);
        } else {
            InventoryEquipmentMover equipmentMover = e -> {
                resolveDraggedDestination(mouseX, mouseY, hoveredHero, cr, e);
                controller.setDragged(null);
            };
            dragged.moveEquipment(equipmentMover, null);
        }
    }

    private void moveCountableEquipment(double mouseX, double mouseY, Creature hoveredHero, Creature cr,
                                        EquipmentMayCountable<?,?> countable, Integer amount) {
        EquipmentMayCountable<?,?>[] toLeave = new EquipmentMayCountable[1];
        toLeave[0] = countable.cloneEquipment(true);
        toLeave[0].setAmount(0);
        EquipmentMayCountable<?,?>[] toMove = new EquipmentMayCountable[1];
        toMove[0] = countable.cloneEquipment(false);
        toMove[0].setAmount(amount);

        InventoryMoveAction countableMoveAction = () -> {
            hookUpRemovableEvents();

            EquipmentMayCountable<?,?> equipmentToLeave = toLeave[0];
            if (equipmentToLeave.getAmount() > 0) {
                moveDraggedBack(cr, equipmentToLeave, countable.getPos());
            }

            EquipmentMayCountable<?,?> equipmentToMove = toMove[0];
            if (equipmentToMove.getAmount() > 0) {
                resolveDraggedDestination(mouseX, mouseY, hoveredHero, cr, equipmentToMove);
            }
        };
        countableRelocationWindow.setToLeave(toLeave);
        countableRelocationWindow.setToMove(toMove);
        countableRelocationWindow.setMaxAmount(amount);

        countableRelocationWindow.setVisible(true);
        countableRelocationWindow.setMoveAction(countableMoveAction);
        removeRemovableEvents();
    }

    private void resolveDraggedDestination(double mouseX, double mouseY, Creature hoveredHero, Creature cr,
                                           Equipment<?,?> equipmentToMove) {
        boolean isMovedToAnotherHero = tryMoveToAnotherHero(hoveredHero, origin, equipmentToMove);
        if (isMovedToAnotherHero) {
            return;
        }

        boolean isMovedWithinInventory = tryMoveEquipmentWithinInventoryElements(mouseX, mouseY, cr, equipmentToMove);
        if (isMovedWithinInventory) {
            return;
        }
        moveDraggedBack(cr, equipmentToMove, null);
    }

    private void startDragScrollBar(double x, double y) {
        for (InventoryViewElement element : inventoryElements) {
            if (element.tryStartDragScroll(x, y)) {
                setDragScrollView(element);
                break;
            }
        }
    }

    private void setDragScrollView(InventoryViewElement element) {
        element.setIsScrollDragged(true);
        dragScrolledView = element;
    }

    private boolean tryMoveToAnotherHero(Creature hero, InventoryViewElement view, Equipment<?, ?> dragged) {
        if (view == null) return false;
        if (hero == null) return false;
        Creature cr = controller.getCreatureToOpenInventory();
        CreatureSize size = cr.getSize();
        if (hero.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (hero.getInventory().tryAdd(dragged, true)) {
                view.setMovedToHeroEquipmentPos(dragged.getPos());
                controller.getLogger().logItemMovedToInventory(dragged.getName(), hero.getName());
                return true;
            }
        } else {
            controller.getLogger().logOneCreatureOutOfAnotherCreatureRange(hero.getName(), cr.getName());
        }
        return false;
    }

    private boolean tryMoveEquipmentWithinInventoryElements(double mouseX, double mouseY, Creature cr, Equipment<?,?> equipmentToMove) {
        InventoryViewElement viewElement = getView(mouseX, mouseY);
        if (viewElement == null) {
            return false;
        }
        mousePos.x = mouseX;
        mousePos.y = mouseY;
        Coords fixedDraggedPos = viewElement.getFixedDraggedPos(mousePos, imgRelativeCoords, temp1,
                        equipmentToMove, draggedInitWidth, draggedInitHeight);

        boolean doMergeCountable = false;
        if (equipmentToMove.isCountable()) {
            Coords local = viewElement.getLocalCoords();
            doMergeCountable = viewElement.lookForEquipment(local.x, local.y, temp2) != null;
        }

        return viewElement.tryAdd(equipmentToMove, cr, fixedDraggedPos.x, fixedDraggedPos.y, doMergeCountable);
    }

    private void moveDraggedBack(Creature cr, Equipment<?,?> dragged, Coords backPos) {
        if (backPos == null) {
            backPos = origin.getExtremePos(mousePos, imgRelativeCoords, dragged);
        }
        double x = 0;
        double y = 0;
        if (backPos != null) {
            x = backPos.x;
            y = backPos.y;
        }
        boolean draggedCannotBeMovedBack = !origin.tryAdd(dragged, cr, x, y, false);
        if (draggedCannotBeMovedBack) {
            Coords draggedPos = dragged.getPos();
            draggedPos.x = 0;
            draggedPos.y = 0;
            dragged.addItemToEquipmentList(cr.getEquipmentList());
        }
    }

    private InventoryViewElement getView(double x, double y) {
        for (InventoryViewElement element : inventoryElements) {
            double evLeft = element.getViewPos().x;
            double evRight = evLeft + element.getViewWidth();
            double evTop = element.getViewPos().y;
            double evBottom = evTop + element.getViewHeight();

            if (x > evLeft && x < evRight
                    && y > evTop && y < evBottom) {
                return element;
            }
        }
        return null;
    }

    private void closeInventory() {
        Equipment<?,?> dragged = controller.getDragged();
        if (dragged != null) {
            Creature cr = controller.getCreatureToOpenInventory();
            holdView.tryAdd(dragged, cr, 0, 0, true);
            controller.setDragged(null);
        }
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScroll);
        controller.closeInventory();
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
        Equipment<?,?> dragged = controller.getDragged();
        if (dragged != null) {
            playDraggedAnimation(mousePos);

            Image img = dragged.getImage().getFxImage();
            gc.drawImage(img,
                    mousePos.x*meter - img.getWidth()/2,
                    mousePos.y*meter - img.getHeight());
        }
    }

    private void playDraggedAnimation(Coords mousePos) {
        InventoryViewElement element = getView(mousePos.x, mousePos.y);
        Equipment dragged = controller.getDragged();
        EquipmentAnimationPos animationPos = dragged.getAnimationPos();
        if (element != null) {
            element.updateCurAnimation(lastCheckedView, animationPos);
        }
        lastCheckedView = element;
        dragged.getAnimation().play(dragged);
    }

    private Coords getMousePos(double mouseX, double mouseY, double left, double top) {
        int meter = Sizes.getMeter();
        mousePos.x = (mouseX - left) / meter;
        mousePos.y = (mouseY - top) / meter;
        return mousePos;
    }

    private void drawHold(List<Equipment<?,?>> items, double inventoryWidth, Creature cr) {
        Inventory inventory = cr.getInventory();

        int meter = Sizes.getMeter();
        double canvasHeight = canvas.getHeight();
        double holdWidth = HOLD_WIDTH * inventoryWidth;
        double holdHeight = HOLD_HEIGHT * canvasHeight / meter;
        double x = HOLD_X_POS * inventoryWidth;
        double y = HOLD_Y_POS * canvasHeight / meter;

        holdView.setInventoryWidth(inventoryWidth);
        holdView.setDragged(controller.getDragged());
        holdView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / meter);
        holdView.setViewPos(x, y);
        holdView.setSize(holdWidth, holdHeight);
        holdView.setInventory(inventory);
        holdView.setSortedEquipment(items);
        holdView.refresh();
    }

    private void drawDrop(List<PosItem<?,?>> sortedItems, double inventoryWidth, Creature cr) {
        List<Equipment<?,?>> equipmentWithinRange;
        synchronized (controller.getGameRunner()) {
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
        dropView.setSortedItems(sortedItems);
        dropView.refresh();
    }

    private void drawContainer(List<Equipment<?,?>> sortedContainerItems, double inventoryWidth) {
        Creature cr = controller.getCreatureToOpenInventory();
        Container con = controller.getContainerToOpen();
        if (con == null) return;
        CreatureSize size = cr.getSize();
        boolean inventoryNotContainContainerToOpen = !holdView.getSortedEquipment().contains(con);
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
        containerView.setDragged(controller.getDragged());
        containerView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / meter);
        containerView.setViewPos(x, y);
        containerView.setSize(width, height);
        containerView.setContainer(con);
        containerView.setSortedEquipment(sortedContainerItems);
        containerView.refresh();
    }

    private void drawCountableRelocationWindow(double inventoryWidth) {
        boolean isNotVisible = !countableRelocationWindow.isVisible();
        if (isNotVisible) return;
        double width = inventoryWidth / 4;
        countableRelocationWindow.setWidth(width);
        double posX = (inventoryWidth-width) / 2;
        countableRelocationWindow.setPosX(posX);
        double canvasHeight = canvas.getHeight() / Sizes.getMeter();
        double height = canvasHeight / 4;
        countableRelocationWindow.setHeight(height);
        double posY = (canvasHeight-height) / 2;
        countableRelocationWindow.setPosY(posY);
        countableRelocationWindow.refresh();
    }

    private void drawCreature(double inventoryWidth, Creature cr) {
        int meter = Sizes.getMeter();
        double creatureViewWidth = CREATURE_WIDTH * inventoryWidth;
        double creatureViewHeight = CREATURE_HEIGHT * canvas.getHeight() / meter;
        double x = CREATURE_X_POS * inventoryWidth;
        double y = CREATURE_Y_POS * canvas.getHeight() / meter;

        creatureView.setDrawnCreature(cr);
        creatureView.setDragged(controller.getDragged());
        creatureView.setViewPos(x, y);
        creatureView.setSize(creatureViewWidth, creatureViewHeight);
        creatureView.refresh();
    }

    private void drawBackground(double inventoryWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, inventoryWidth * Sizes.getMeter(), canvas.getHeight());
    }

    public DropViewElement getDropView() {
        return dropView;
    }

    public HoldViewElement getHoldView() {
        return holdView;
    }

    public ContainerViewElement getContainerView() {
        return containerView;
    }
}
