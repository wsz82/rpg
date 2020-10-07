package game.view.world.inventory;

import game.model.GameController;
import game.view.world.FoggableDelegate;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.draw.ItemsDrawer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class DropViewElement extends EquipmentViewElement {
    private final Coords creaturePos = new Coords();
    private final FoggableDelegate foggableDelegate;
    private final ItemsDrawer drawer;

    private List<PosItem<?,?>> sortedItems;
    private List<Equipment<?,?>> droppedEquipment;
    private double visionWidthDiameter;
    private double visionHeightDiameter;
    private double minCurPosY;
    private double minCurPosX;
    private double maxCurPosX;
    private double xScrollButtonWidth;
    private boolean isXScrollVisible;
    private boolean isNotHorizontallyScrolled;

    public DropViewElement(Canvas canvas, GameController gameController, Coords mousePos) {
        super(canvas, gameController, mousePos);
        this.foggableDelegate = new FoggableDelegate(gameController, canvas, curPos, viewPos);
        this.drawer = new ItemsDrawer();
        initDrawer();
    }

    private void initDrawer() {
        drawer.setEquipmentDrawer(this::playDropAnimation);
    }

    @Override
    public void refreshElement() {
        super.refreshElement();

        drawHorScroll();

        drawWithinOval();

        updateCurPos();
    }

    @Override
    protected void setAppropriateCursor() {
        if (isMouseNotWithinView()) return;
        Coords localCoords = getLocalCoords();
        Creature creatureToOpenInventory = controller.getCreatureToOpenInventory();
        Location currentLocation = controller.getCurrentLocation();
        double width = currentLocation.getWidth();
        double height = currentLocation.getHeight();
        setAppropriateCursor(creatureToOpenInventory, localCoords, 0, 0, width, height, getSortedEquipment());
    }

    private void updateCurPos() {
        Location currentLocation = controller.getCurrentLocation();
        double maxCurPosX = currentLocation.getWidth() - viewWidth;
        double maxCurPosY = currentLocation.getHeight() - viewHeight;
        if (curPos.x < 0) {
            curPos.x = 0;
        } else if (curPos.x > maxCurPosX){
            curPos.x = maxCurPosX;
        }
        if (curPos.y < 0) {
            curPos.y = 0;
        } else if (curPos.y > maxCurPosY) {
            curPos.y = maxCurPosY;
        }
    }

    void drawWithinOval() {
        gc.save();
        gc.beginPath();
        double centerX = (viewPos.x + viewWidth/2) * Sizes.getMeter();
        double centerY = (viewPos.y + viewHeight/2) * Sizes.getMeter();
        double radiusX = viewWidth / 2 * Sizes.getMeter();
        double radiusY = viewHeight / 2 * Sizes.getMeter();
        int startAngle = 0;
        int length = 360;
        gc.arc(centerX, centerY, radiusX, radiusY, startAngle, length);
        gc.closePath();
        gc.clip();

        drawEquipment();
        drawFog();

        gc.restore();
    }

    private void drawFog() {
        foggableDelegate.drawFog(viewWidth, viewHeight);
    }

    @Override
    protected void scrollScrollBar() {
        if (isNotHorizontallyScrolled) {
            super.scrollScrollBar();
        } else {
            if (!isXScrollVisible || isViewNotVisible()) return;
            double scrollToX = mousePos.x - viewPos.x;
            setCurPosX(scrollToX);
        }
    }

    private void drawHorScroll() {
        isXScrollVisible = !(visionWidthDiameter <= viewWidth);
        if (!isXScrollVisible) {
            return;
        }
        int meter = Sizes.getMeter();
        double y = (viewPos.y + viewHeight) * meter;

        clearHorScroll(y);
        drawHorScrollButton(y);
    }

    private void clearHorScroll(double y) {
        gc.setFill(Color.BLUE);
        int meter = Sizes.getMeter();
        gc.fillRect(viewPos.x * meter, y, viewWidth * meter, scrollWidth * meter);
    }

    @Override
    protected final void drawEquipment() {
        Creature cr = controller.getCreatureToOpenInventory();
        for (PosItem<?,?> item : sortedItems) {
            adjustCoverOpacity(cr, item);

            if (item == cr) {
                drawCreatureBase(cr);
            }

            Coords pos = item.getPos();
            Coords corrected = currentPosCorrection(pos);
            int meter = Sizes.getMeter();
            double x = corrected.x * meter;
            double y = corrected.y * meter;
            item.draw(drawer);
            Image img = item.getImage().getFxImage();
            double viewX = viewPos.x * meter;
            double viewY = viewPos.y * meter;
            double viewWidth = this.viewWidth * meter;
            double viewHeight = this.viewHeight * meter;

            cutImageAndDraw(x, y, img, viewX, viewY, viewWidth, viewHeight);
            gc.setGlobalAlpha(1.0);
        }
    }

    @Override
    protected void drawVerScroll() {
        isYScrollVisible = !(visionHeightDiameter <= viewHeight);
        if (!isYScrollVisible) {
            return;
        }
        int meter = Sizes.getMeter();
        double x = (viewPos.x + viewWidth) * meter;

        clearVerScroll(x);
        drawVerScrollButton(x);
    }

    @Override
    protected void drawVerScrollButton(double x) {
        minCurPosY = creaturePos.y - visionHeightDiameter/2;
        minCurPosY = Math.max(0, minCurPosY);
        maxCurPosY = creaturePos.y + visionHeightDiameter/2;
        maxCurPosY = Math.min(controller.getCurrentLocation().getHeight(), maxCurPosY);
        double verticalDifference = maxCurPosY - minCurPosY;
        yScrollPos = (curPos.y - minCurPosY) * viewHeight / verticalDifference;
        double y = viewPos.y + yScrollPos;
        yScrollButtonHeight = viewHeight * viewHeight / verticalDifference;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x, y * meter, scrollWidth * meter, yScrollButtonHeight * meter);
    }

    private void drawHorScrollButton(double y) {
        minCurPosX = creaturePos.x - visionWidthDiameter/2;
        minCurPosX = Math.max(0, minCurPosX);
        maxCurPosX = creaturePos.x + visionWidthDiameter/2;
        maxCurPosX = Math.min(controller.getCurrentLocation().getWidth(), maxCurPosX);
        double horizontalDifference = maxCurPosX - minCurPosX;
        double xScrollPos = (curPos.x - minCurPosX) * viewWidth / horizontalDifference;
        double x = viewPos.x + xScrollPos;
        xScrollButtonWidth = viewWidth * viewWidth / horizontalDifference;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x * meter, y, xScrollButtonWidth * meter, scrollWidth * meter);
    }

    private void drawCreatureBase(Creature cr) {
        Coords pos = cr.getCenter();
        Coords corrected = currentPosCorrection(pos);
        double x = (corrected.x + viewPos.x);
        double y = (corrected.y + viewPos.y);
        ResolutionImage base = cr.getBase();
        drawCreatureBase(x, y, base);
    }

    public void setDroppedEquipment(List<Equipment<?,?>> droppedEquipment) {
        this.droppedEquipment = droppedEquipment;
    }

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BLACK);
        gc.fillOval(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    @Override
    public boolean tryRemove(Equipment<?, ?> e, Creature cr) {
        if (e.tryTake(cr)) {
            droppedEquipment.remove(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean tryAdd(Equipment<?, ?> e, Creature cr, double x, double y, boolean doMergeCountable) {
        return e.tryDrop(cr, x, y);
    }

    @Override
    public boolean tryStartDragScroll(double x, double y) {
        if (super.tryStartDragScroll(x, y)) {
            isNotHorizontallyScrolled = true;
            return true;
        }
        double left = viewPos.x;
        double right = left + viewWidth;
        double top = viewPos.y;
        double bottom = top + viewHeight;

        if (isXScrollVisible) {
            boolean isPointWithinScrollBar = x > left && x < right
                    && y > bottom && y < bottom + scrollWidth;
            if (isPointWithinScrollBar) {
                setCurPosX(x - left);
                isNotHorizontallyScrolled = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment<?, ?> e) {
        return e.getPos();
    }

    @Override
    public void setMovedToHeroEquipmentPos(Coords pos) {
        pos.x = 0;
        pos.y = 0;
    }

    @Override
    public List<Equipment<?, ?>> getSortedEquipment() {
        return droppedEquipment;
    }

    @Override
    public void setCurPosY(double y) {
        y -= yScrollButtonHeight/2;
        double maxY;
        if (y < 0) {
            curPos.y = minCurPosY;
        } else if (y > (maxY = viewHeight - yScrollButtonHeight)) {
            curPos.y = maxY * ((maxCurPosY - minCurPosY) / viewHeight) + minCurPosY;
        } else {
            curPos.y = y * ((maxCurPosY - minCurPosY) / viewHeight) + minCurPosY;
        }
    }

    @Override
    public void updateCurAnimation(InventoryViewElement lastCheckedView, EquipmentAnimationPos animationPos) {
        if (lastCheckedView != null) {
            lastCheckedView.resetAnimationPosForInventoryToDropTransition(animationPos);
        }
        animationPos.setCurAnimation(EquipmentAnimationType.DROP);
    }

    @Override
    protected void resetAnimationPosForDropToInventoryTransition(EquipmentAnimationPos animationPos) {
        animationPos.setNextFrameUpdate(0);
    }

    @Override
    protected void resetAnimationPosForInventoryToDropTransition(EquipmentAnimationPos animationPos) {}

    public void setVisionWidthDiameter(double visionWidthDiameter) {
        this.visionWidthDiameter = visionWidthDiameter;
    }

    public void setVisionHeightDiameter(double visionHeightDiameter) {
        this.visionHeightDiameter = visionHeightDiameter;
    }

    public void setCreaturePos(double x, double y) {
        if (x != creaturePos.x) {
            creaturePos.x = x;
            curPos.x = creaturePos.x - viewWidth/2;
        }
        if (y != creaturePos.y) {
            creaturePos.y = y;
            curPos.y = creaturePos.y - viewHeight/2;
        }
    }

    public void setCurPosX(double x) {
        x -= xScrollButtonWidth/2;
        double maxX;
        if (x < 0) {
            curPos.x = minCurPosX;
        } else if (x > (maxX = viewWidth - xScrollButtonWidth)) {
            curPos.x = maxX * ((maxCurPosX - minCurPosX) / viewWidth) + minCurPosX;
        } else {
            curPos.x = x * ((maxCurPosX - minCurPosX) / viewWidth) + minCurPosX;
        }
    }

    public double getMinCurPosY() {
        return minCurPosY;
    }

    public void setSortedItems(List<PosItem<?,?>> sortedItems) {
        this.sortedItems = sortedItems;
    }
}