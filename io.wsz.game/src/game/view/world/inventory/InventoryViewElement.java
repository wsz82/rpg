package game.view.world.inventory;

import game.model.GameController;
import game.view.world.CanvasView;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;

public abstract class InventoryViewElement extends CanvasView {
    protected final Coords temp1 = new Coords();
    protected final Coords temp2 = new Coords();
    protected final List<Equipment<?,?>> lookedEquipment = new ArrayList<>(0);
    protected final Coords viewPos = new Coords();

    protected double viewWidth;
    protected double viewHeight;
    protected boolean isScrollDragged;
    protected Equipment<?,?> dragged;

    public InventoryViewElement(Canvas canvas, GameController gameController, Coords mousePos) {
        super(canvas, gameController);
        this.mousePos = mousePos;
    }

    public void refresh() {
        if (isViewNotVisible()) return;
        if (isScrollDragged) {
            scrollScrollBar();
        }
    }

    protected boolean isViewNotVisible() {
        return viewWidth < 0 || viewHeight < 0;
    }

    protected boolean isMouseNotWithinView() {
        double mouseX = mousePos.x;
        double viewX = viewPos.x;
        double mouseY = mousePos.y;
        double viewY = viewPos.y;
        return mouseX < viewX || mouseY < viewY || mouseX > viewX + viewWidth || mouseY > viewY + viewHeight;
    }

    protected abstract void scrollScrollBar();

    public abstract boolean tryRemove(Equipment<?,?> e, Creature cr);

    public abstract boolean tryAdd(Equipment<?,?> e, Creature cr, double x, double y, boolean doMergeCountable);

    public abstract Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment<?,?> e);

    public Coords getFixedDraggedPos(Coords mousePos, Coords draggedCoords, Coords translated,
                                     Equipment<?,?> dragged, double draggedInitWidth, double draggedInitHeight) {
        return getLocalCoords(translated);
    }

    public Coords getLocalCoords() {
        return getLocalCoords(temp1);
    }

    public Coords getLocalCoords(Coords translated) {
        translated.x = mousePos.x;
        translated.y = mousePos.y;
        translated.subtract(viewPos);
        return translated;
    }

    public abstract Equipment<?,?> lookForEquipment(double x, double y, Coords draggedCoords);

    public abstract void scrollUp();

    public abstract void scrollDown();

    public abstract boolean tryStartDragScroll(double x, double y);

    public void setMovedToHeroEquipmentPos(Coords pos) {
    }

    public void updateCurAnimation(InventoryViewElement lastCheckedView, EquipmentAnimationPos animationPos) {
        if (lastCheckedView != null) {
            lastCheckedView.resetAnimationPosForDropToInventoryTransition(animationPos);
        }
        animationPos.setCurAnimation(EquipmentAnimationType.INVENTORY);
    }

    protected void resetAnimationPosForDropToInventoryTransition(EquipmentAnimationPos animationPos) {}

    protected void resetAnimationPosForInventoryToDropTransition(EquipmentAnimationPos animationPos) {
        animationPos.setNextFrameUpdate(0);
    }

    protected double getScrollSpeed() {
        return controller.getSettings().getDialogScrollSpeed();
    }

    public void setSize(double width, double height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    public void setViewPos(double x, double y) {
        viewPos.x = x;
        viewPos.y = y;
    }

    public Coords getViewPos() {
        return viewPos;
    }

    public void setDragged(Equipment<?,?> e) {
        dragged = e;
    }

    public void setIsScrollDragged(boolean isScrollDragged) {
        this.isScrollDragged = isScrollDragged;
    }
}
