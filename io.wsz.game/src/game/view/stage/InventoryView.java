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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryView {
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
    private final Equipment[] dragged = new Equipment[1];

    private EventHandler<MouseEvent> equipmentLook;
    private EventHandler<MouseEvent> dragStop;
    private EventHandler<KeyEvent> closeEvent;

    private EquipmentView origin;

    public InventoryView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        this.holdView = new HoldView(gc);
        equipmentViews.add(holdView);
        this.dropView = new DropView(gc);
        equipmentViews.add(dropView);
        this.containerView = new ContainerView(gc);
        equipmentViews.add(containerView);

        hookupEvents();
    }

    private void hookupEvents() {
        closeEvent = e -> {
            synchronized (GameController.get().getGameRunner()) {
                KeyCode code = e.getCode();
                if (code.equals(KeyCode.I) || code.equals(KeyCode.ESCAPE)) {
                    e.consume();
                    closeInventory();
                }
            }
        };
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, closeEvent);

        equipmentLook = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                mousePos.x = e.getX() / Sizes.getMeter();
                mousePos.y = e.getY() / Sizes.getMeter();
                synchronized (GameController.get().getGameRunner()) {
                    startDrag(mousePos.x, mousePos.y);
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, equipmentLook);

        dragStop = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                if (dragged[0] != null) {
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
    }

    private void startDrag(double mouseX, double mouseY) {
        EquipmentView ev = getEquipmentView(mouseX, mouseY);
        if (ev == null) return;
        origin = ev;
        translateScreenCoordsToCoords(mousePos, ev.getCurrentPos(), ev.getViewPos());
        Equipment eq = lookForEquipment(modifiedCoords.x, modifiedCoords.y, ev);
        Container con = Controller.get().getContainerToOpen();
        if (eq == con) return;
        if (eq != null) {
            ev.remove(eq, Controller.get().getCreatureToOpenInventory());
            dragged[0] = eq.cloneEquipment();
        }
    }

    private void stopDrag(double mouseX, double mouseY) {
        EquipmentView ev = getEquipmentView(mouseX, mouseY);
        Equipment toAdd = dragged[0];
        if (toAdd == null) return;
        Creature cr = Controller.get().getCreatureToOpenInventory();
        if (ev == null) {
            Coords extreme = origin.getExtremePos(mousePos, draggedEquipmentCoords, toAdd);
            origin.add(toAdd, cr, extreme.x, extreme.y);
        } else {
            Coords local = ev.getLocalCoords(mousePos);
            local.subtract(draggedEquipmentCoords);
            checkFit(ev, toAdd, local);
            ev.add(toAdd, cr, local.x, local.y);
        }
        dragged[0] = null;
    }

    private void checkFit(EquipmentView ev, Equipment e, Coords local) {
        Coords currentPos = ev.getCurrentPos();
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
        for (EquipmentView ev : equipmentViews) {

            double evLeft = ev.getViewPos().x;
            double evRight = evLeft + ev.getViewWidth();
            double evTop = ev.getViewPos().y;
            double evBottom = evTop + ev.getViewHeight();

            if (x > evLeft && x < evRight
                    && y > evTop && y < evBottom) return ev;
        }
        return null;
    }

    private void closeInventory() {
        Equipment drag = dragged[0];
        if (drag != null) {
            Creature cr = Controller.get().getCreatureToOpenInventory();
            holdView.add(drag, cr, 0, 0);
            dragged[0] = null;
        }
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, equipmentLook);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, dragStop);
        canvas.removeEventHandler(KeyEvent.KEY_RELEASED, closeEvent);
        Controller.get().closeInventory();
    }

    public void refresh() {
        double width = canvas.getWidth();
        if (width == 0) return;
        double right = (width - Settings.getBarPart()*width) / Sizes.getMeter();

        Creature cr = Controller.get().getCreatureToOpenInventory();

        drawBackground(right);

        drawCreature(right, cr);

        drawHold(right, cr);

        drawDrop(right, cr);

        drawContainer(right);

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

        Equipment drag = dragged[0];
        Coords mousePos = getMousePos(x, y, left, top);
        if (drag != null) {
            Image img = drag.getImage();
            gc.drawImage(img,
                    mousePos.x*Sizes.getMeter() - img.getWidth()/2,
                    mousePos.y*Sizes.getMeter() - img.getHeight());
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

        double x = 0.6 * inventoryWidth;
        double y = 0.2 * canvas.getHeight() / Sizes.getMeter();
//        dropView.setCreaturePos(cr.getCenterBottomPos());
        double range = cr.getRange();

        double height = cr.getSize().getHeight() + 2*range;
        double width = cr.getSize().getWidth() + 2*range;

        double maxDropHeight = 0.3 * canvas.getHeight() / Sizes.getMeter();
        double maxDropWidth = 0.3 * inventoryWidth;

        double resultHeight = height;
        double resultWidth = width;

        if (height >= maxDropHeight) {
            resultHeight = maxDropHeight;
            double ratio = maxDropWidth/height;
            resultWidth = width*ratio;
        } else if (width >= maxDropWidth) {
            resultWidth = maxDropWidth;
            double ratio = maxDropHeight/width;
            resultHeight = height*ratio;
        }

        dropView.setViewPos(x, y);
        Coords bottom = cr.getCenter();
        dropView.setCurrentPos(bottom.x - resultWidth/2, bottom.y - resultHeight/2);
        dropView.setSize(resultWidth, resultHeight);
        dropView.setDroppedEquipment(equipmentWithinRange);
        dropView.refresh();
    }

    private void drawContainer(double inventoryWidth) {
        Creature cr = Controller.get().getCreatureToOpenInventory();
        Container c = Controller.get().getContainerToOpen();
        if (c == null) return;
        CreatureSize size = cr.getSize();
        if (!c.withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) return;
        double width = 0.3 * inventoryWidth;
        double height = 0.3 * canvas.getHeight() / Sizes.getMeter();
        double x = 0.3 * inventoryWidth;
        double y = 0.2 * canvas.getHeight() / Sizes.getMeter();

        containerView.setViewPos(x, y);
        containerView.setSize(width, height);
        containerView.setContainer(c);
        containerView.refresh();
    }

    private void drawCreature(double inventoryWidth, Creature cr) {
        Image img = cr.getImage();

        double x = 0.1 * inventoryWidth * Sizes.getMeter();
        double y = 0.2 * canvas.getHeight();

        gc.drawImage(img, x, y);
    }

    private void drawBackground(double inventoryWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, inventoryWidth * Sizes.getMeter(), canvas.getHeight());
    }

    public Equipment lookForEquipment(double x, double y, EquipmentView ev) {
        Coords currentPos = ev.getCurrentPos();
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
