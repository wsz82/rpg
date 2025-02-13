package game.view.world.inventory;

import game.model.GameController;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class HoldViewElement extends EquipmentViewElement {
    private Inventory inventory;

    public HoldViewElement(Canvas canvas, GameController gameController, Coords mousePos) {
        super(canvas, gameController, mousePos);
    }

    @Override
    public void refreshElement() {
        super.refreshElement();

        drawEquipment();

        drawHoldSize();

        drawHoldWeight();
    }

    private void drawHoldWeight() {
        double actualWeight = inventory.getActualWeight();
        double maxWeight = inventory.getMaxWeight();
        double columnWidth = WEIGHT_COLUMN_WIDTH * inventoryWidth;
        double viewHeight = this.viewHeight;
        double sizeColumnWidth = SIZE_COLUMN_WIDTH * inventoryWidth;
        double columnX = viewPos.x - columnWidth - sizeColumnWidth;
        double columnY = viewPos.y;
        Color backgroundColor = Color.BLACK;
        drawColumn(actualWeight, maxWeight, columnWidth, viewHeight, columnX, columnY, backgroundColor);
    }

    private void drawHoldSize() {
        double filledSpace = inventory.getFilledSpace();
        double maxSize = inventory.getMaxSize();
        double columnWidth = SIZE_COLUMN_WIDTH * inventoryWidth;
        double viewHeight = this.viewHeight;
        double columnX = viewPos.x - columnWidth;
        double columnY = viewPos.y;
        Color backgroundColor = Color.WHITE;
        drawColumn(filledSpace, maxSize, columnWidth, viewHeight, columnX, columnY, backgroundColor);
    }

    @Override
    protected final void drawEquipment() {
        if (sortedEquipment == null) return;
        for (Equipment e : sortedEquipment) {
            Coords pos = e.getPos();
            Coords corrected = currentPosCorrection(pos);
            int meter = Sizes.getMeter();
            double x = corrected.x * meter;
            double y = corrected.y * meter;
            playInventoryAnimation(e);
            Image img = e.getImage().getFxImage();
            double viewX = viewPos.x * meter;
            double viewY = viewPos.y * meter;
            double viewWidth = this.viewWidth * meter;
            double viewHeight = this.viewHeight * meter;

            cutImageAndDraw(x, y, img, viewX, viewY, viewWidth, viewHeight);
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BROWN);
        gc.fillRect(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    @Override
    public boolean tryRemove(Equipment<?, ?> e, Creature cr) {
        inventory.remove(e);
        controller.getLogger().logItemRemovedFromInventory(e.getName(), cr.getName());
        return true;
    }

    @Override
    public boolean tryAdd(Equipment<?, ?> e, Creature cr, double x, double y, boolean doMergeCountable) {
        if (!inventory.tryAdd(e, doMergeCountable)) {
            controller.getLogger().logItemDoesNotFitInventory(e.getName(), cr.getName());
            return false;
        } else {
            e.setPos(x, y, 0, null);
            controller.getLogger().logItemAddedToInventory(e.getName(), cr.getName());
            return true;
        }
    }

    @Override
    public List<Equipment<?, ?>> getSortedEquipment() {
        return sortedEquipment;
    }
}
