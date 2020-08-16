package game.view.stage;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.Controller;
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
import java.util.Collections;
import java.util.List;

public class InventoryView {
    protected static final double SCROLL_BUTTON_PART = 1.0/80;
    private static final double CREATURE_X_POS = 0.05;
    private static final double CREATURE_Y_POS = 0.1;
    private static final double CREATURE_WIDTH = 0.2;
    private static final double CREATURE_HEIGHT = 0.4;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Coords mousePos = new Coords();
    private final Coords modifiedCoords = new Coords();
    private final Coords draggedEquipmentCoords = new Coords();
    private final List<Equipment> lookedEquipment = new ArrayList<>(0);
    private final List<EquipmentView> equipmentViews = new ArrayList<>(0);
    private final HoldView holdView;
    private final DropView dropView;
    private final ContainerView containerView;

    private Equipment dragged;
    private EventHandler<MouseEvent> onClick;
    private EventHandler<MouseEvent> dragStop;
    private EventHandler<KeyEvent> closeEvent;
    private EventHandler<ScrollEvent> wheelScroll;
    private EquipmentView origin;
    private EquipmentView scrolledVer;
    private DropView scrolledHor;

    public InventoryView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        this.holdView = new HoldView(canvas);
        equipmentViews.add(holdView);
        this.dropView = new DropView(canvas);
        equipmentViews.add(dropView);
        this.containerView = new ContainerView(canvas);
        equipmentViews.add(containerView);

        hookupEvents();
    }

    private void hookupEvents() {
        closeEvent = e -> {
            KeyCode code = e.getCode();
            if (code.equals(KeyCode.I) || code.equals(KeyCode.ESCAPE)) {
                e.consume();
                synchronized (GameController.get().getGameRunner()) {
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
                synchronized (GameController.get().getGameRunner()) {
                    startDrag(mousePos.x, mousePos.y);
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (GameController.get().getGameRunner()) {
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
                    synchronized (GameController.get().getGameRunner()) {
                        stopDrag(mousePos.x, mousePos.y);
                    }
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);

        wheelScroll = e -> {
            e.consume();
            EquipmentView ev = getEquipmentView(mousePos.x, mousePos.y);
            if (ev == null) {
                return;
            }
            double dY = e.getDeltaY();
            if (dY < 0) {
                scrollDown(ev);
            } else {
                scrollUp(ev);
            }
        };
        canvas.addEventHandler(ScrollEvent.SCROLL, wheelScroll);
    }

    private void scrollDown(EquipmentView ev) {
        Coords curPos = ev.getCurPos();
        double y = curPos.y;
        double maxPos = ev.getMaxCurPosY() - ev.getViewHeight();
        if (y >= maxPos) {
            return;
        }
        double newY = y + getScrollSpeed();
        newY = Math.min(newY, maxPos);
        curPos.y = newY;
    }

    private double getScrollSpeed() {
        return Settings.getDialogScrollSpeed();
    }

    private void scrollUp(EquipmentView ev) {
        Coords curPos = ev.getCurPos();
        double curPosY = curPos.y;
        double minCurPosY = ev.getMinCurPosY();
        if (curPosY <= minCurPosY) {
            return;
        }
        double newY = curPosY - getScrollSpeed();
        newY = Math.max(newY, minCurPosY);
        curPos.y = newY;
    }

    private void openContainer() {
        EquipmentView ev = getEquipmentView(mousePos.x, mousePos.y);
        if (ev instanceof ContainerView) return;
        origin = ev;
        translateScreenCoordsToCoords(mousePos, ev.getCurPos(), ev.getViewPos());
        Equipment eq = lookForEquipment(modifiedCoords.x, modifiedCoords.y, ev);
        if (!(eq instanceof Container)) return;
        Container toOpen = (Container) eq;
        Controller controller = Controller.get();
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
        EquipmentView ev = getEquipmentView(mouseX, mouseY);
        if (ev == null) return;
        origin = ev;
        translateScreenCoordsToCoords(mousePos, ev.getCurPos(), ev.getViewPos());
        Equipment eq = lookForEquipment(modifiedCoords.x, modifiedCoords.y, ev);
        Container con = Controller.get().getContainerToOpen();
        if (eq == con) return;
        if (eq != null) {
            if (ev.remove(eq, Controller.get().getCreatureToOpenInventory())) {
                dragged = eq.cloneEquipment();
            }
        }
    }

    private void stopDrag(double mouseX, double mouseY) {
        if (dragged == null) return;
        GameController gameController = GameController.get();
        Creature hoveredHero = gameController.getHoveredHero();
        if (!moveToHero(hoveredHero)) {
            moveEquipmentWithinInventory(mouseX, mouseY);
        }
        dragged = null;
    }

    private boolean moveToHero(Creature hero) {
        if (hero == null) return false;
        Creature cr = Controller.get().getCreatureToOpenInventory();
        CreatureSize size = cr.getSize();
        if (hero.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (hero.getInventory().add(dragged)) {
                System.out.println(dragged.getName() + " moved to " + hero.getName() + " inventory");
                return true;
            }
        } else {
            System.out.println(hero.getName() + " out of " + cr.getName() + " range");
        }
        return false;
    }

    private void moveEquipmentWithinInventory(double mouseX, double mouseY) {
        EquipmentView ev = getEquipmentView(mouseX, mouseY);
        Creature cr = Controller.get().getCreatureToOpenInventory();
        if (ev == null) {
            Coords extreme = origin.getExtremePos(mousePos, draggedEquipmentCoords, dragged);
            origin.add(dragged, cr, extreme.x, extreme.y);
        } else {
            Coords local = ev.getLocalCoords(mousePos);
            local.subtract(draggedEquipmentCoords);
            checkFit(ev, dragged, local);
            ev.add(dragged, cr, local.x, local.y);
        }
    }

    private void checkFit(EquipmentView ev, Equipment e, Coords local) {
        Coords currentPos = ev.getCurPos();
        if (local.x < currentPos.x) {
            local.x = currentPos.x;
        } else {
            double max = currentPos.x + ev.getViewWidth() - e.getImageWidth();
            if (local.x > max) {
                local.x = max;
            }
        }
        if (local.y < currentPos.y) {
            local.y = currentPos.y;
        } else {
            double max = currentPos.y + ev.getViewHeight() - e.getImageHeight();
            if (local.y > max) {
                local.y = max;
            }
        }
    }

    private EquipmentView getEquipmentView(double x, double y) {
        scrolledVer = null;
        scrolledHor = null;
        for (EquipmentView ev : equipmentViews) {

            double evLeft = ev.getViewPos().x;
            double evRight = evLeft + ev.getViewWidth();
            double evTop = ev.getViewPos().y;
            double evBottom = evTop + ev.getViewHeight();

            if (x > evLeft && x < evRight
                    && y > evTop && y < evBottom) return ev;

            double scrollWidth = ev.getScrollWidth();
            if (ev.isYScrollVisible()) {
                double yScrollPos = ev.getYScrollPos();
                double yScrollButtonHeight = ev.getYScrollButtonHeight();
                if (x > evRight && x < evRight + scrollWidth
                        && y > evTop + yScrollPos && y < evTop + yScrollPos + yScrollButtonHeight) {
                    scrolledVer = ev;
                    return null;
                }
                if (x > evRight && x < evRight + scrollWidth
                        && y > evTop && y < evBottom) {
                    ev.setCurPosY(y - evTop);
                }
            }

            if (ev instanceof DropView) {
                DropView dv = (DropView) ev;
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
        }
        return null;
    }

    private void closeInventory() {
        if (dragged != null) {
            Creature cr = Controller.get().getCreatureToOpenInventory();
            holdView.add(dragged, cr, 0, 0);
            dragged = null;
        }
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScroll);
        Controller.get().closeInventory();
    }

    public void refresh() {
        double width = canvas.getWidth();
        if (width == 0) return;
        double barViewWidth = Settings.getBarPart() * width;
        double inventoryWidth = (width - barViewWidth) / Sizes.getMeter();

        Creature cr = Controller.get().getCreatureToOpenInventory();

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

    private Coords getMousePos(double mouseX, double mouseY, double left, double top) {
        mousePos.x = (mouseX - left) / Sizes.getMeter();
        mousePos.y = (mouseY - top) / Sizes.getMeter();
        return mousePos;
    }

    private void drawHold(double inventoryWidth, Creature cr) {
        Inventory inventory = cr.getInventory();

        double holdWidth = 0.6 * inventoryWidth;
        double holdHeight = 0.3 * canvas.getHeight() / Sizes.getMeter();
        double x = 0.3 * inventoryWidth;
        double y = 0.6 * canvas.getHeight() / Sizes.getMeter();

        holdView.setDragged(dragged);
        holdView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / Sizes.getMeter());
        holdView.setViewPos(x, y);
        holdView.setSize(holdWidth, holdHeight);
        holdView.setInventory(inventory);
        holdView.refresh();
    }

    private void drawDrop(double inventoryWidth, Creature cr) {
        List<Equipment> equipmentWithinRange;
        synchronized (GameController.get().getGameRunner()) {
            equipmentWithinRange = cr.getEquipmentWithinRange();
        }

        double scrollWidth = canvas.getWidth() * SCROLL_BUTTON_PART / Sizes.getMeter();
        double x = 0.6 * inventoryWidth + scrollWidth;
        double y = 0.2 * canvas.getHeight() / Sizes.getMeter();
        double range = cr.getRange();

        double height = cr.getSize().getHeight() + 2*range;
        dropView.setVisionHeightDiameter(height);
        double width = cr.getSize().getWidth() + 2*range;
        dropView.setVisionWidthDiameter(width);

        double maxDropHeight = 0.3 * canvas.getHeight() / Sizes.getMeter();
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
        Coords center = cr.getCenter();
        dropView.setSize(resultWidth, resultHeight);
        dropView.setCreaturePos(center.x, center.y);
        dropView.setDroppedEquipment(equipmentWithinRange);
        dropView.refresh();
    }

    private void drawContainer(double inventoryWidth) {
        Creature cr = Controller.get().getCreatureToOpenInventory();
        Container con = Controller.get().getContainerToOpen();
        if (con == null) return;
        CreatureSize size = cr.getSize();
        boolean inventoryNotContainContainerToOpen = !holdView.getItems().contains(con);
        if (inventoryNotContainContainerToOpen) {
            boolean containerOutOfRange = !con.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight());
            if (containerOutOfRange) return;
        }
        double width = 0.3 * inventoryWidth;
        double height = 0.3 * canvas.getHeight() / Sizes.getMeter();
        double x = 0.3 * inventoryWidth;
        double y = 0.2 * canvas.getHeight() / Sizes.getMeter();

        containerView.setDragged(dragged);
        containerView.setScrollWidth(canvas.getWidth() * SCROLL_BUTTON_PART / Sizes.getMeter());
        containerView.setViewPos(x, y);
        containerView.setSize(width, height);
        containerView.setContainer(con);
        containerView.refresh();
    }

    private void drawCreature(double inventoryWidth, Creature cr) {
        int meter = Sizes.getMeter();
        double meterFactor = inventoryWidth * meter;
        double x = CREATURE_X_POS * meterFactor;
        double pictureWidth = CREATURE_WIDTH * meterFactor;
        double y = CREATURE_Y_POS * canvas.getHeight();
        double pictureHeight = CREATURE_HEIGHT * meterFactor;

        Image img = cr.getAnimation().getCreatureInventoryImage(cr, (int) pictureWidth, (int) pictureHeight);
        gc.drawImage(img, x, y);
    }

    private void drawBackground(double inventoryWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, inventoryWidth * Sizes.getMeter(), canvas.getHeight());
    }

    public Equipment lookForEquipment(double x, double y, EquipmentView ev) {
        Coords currentPos = ev.getCurPos();
        if (x < currentPos.x || x > currentPos.x + ev.getViewWidth()) return null;
        if (y < currentPos.y || y > currentPos.y + ev.getViewHeight()) return null;
        lookedEquipment.clear();
        lookedEquipment.addAll(ev.getItems());
        Collections.reverse(lookedEquipment);
        for (Equipment eq : lookedEquipment) {
            double cX = eq.getLeft();
            double cWidth = eq.getImageWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) continue;

            double cY = eq.getTop();
            double cHeight = eq.getImageHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) continue;

            Image img = eq.getImage();
            int imgX = (int) ((x - cX) * Sizes.getMeter());
            int imgY = (int) ((y - cY) * Sizes.getMeter());
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
            if (isPixelTransparent) continue;
            draggedEquipmentCoords.x = (double) imgX / Sizes.getMeter();
            draggedEquipmentCoords.y = (double) imgY / Sizes.getMeter();
            return eq;
        }
        return null;
    }

    private void translateScreenCoordsToCoords(Coords pos, Coords currentPos, Coords viewPos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.add(currentPos);
        modifiedCoords.subtract(viewPos);
    }
}
