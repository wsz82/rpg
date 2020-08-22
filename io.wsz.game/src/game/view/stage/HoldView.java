package game.view.stage;

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

public class HoldView extends EquipmentView {
    private Inventory inventory;

    public HoldView(Canvas canvas, GameController gameController) {
        super(canvas, gameController);
    }

    @Override
    public void refresh() {
        super.refresh();

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
        int filledSpace = inventory.getFilledSpace();
        int maxSize = inventory.getMaxSize();
        double columnWidth = SIZE_COLUMN_WIDTH * inventoryWidth;
        double viewHeight = this.viewHeight;
        double columnX = viewPos.x - columnWidth;
        double columnY = viewPos.y;
        Color backgroundColor = Color.WHITE;
        drawColumn(filledSpace, maxSize, columnWidth, viewHeight, columnX, columnY, backgroundColor);
    }

    @Override
    protected final void drawEquipment() {

        selectEquipment();

        for (Equipment e : items) {
            Coords pos = e.getPos();
            Coords corrected = currentPosCorrection(pos);
            int meter = Sizes.getMeter();
            double x = corrected.x * meter;
            double y = corrected.y * meter;
            playInventoryAnimation(e);
            Image img = e.getImage();
            double viewX = viewPos.x * meter;
            double viewY = viewPos.y * meter;
            double viewWidth = this.viewWidth * meter;
            double viewHeight = this.viewHeight * meter;

            cutImageAndDraw(x, y, img, viewX, viewY, viewWidth, viewHeight);
        }
    }

    public Inventory getInventory() {
        return inventory;
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
    public boolean remove(Equipment e, Creature cr) {
        inventory.remove(e);
        System.out.println(e.getName() + " removed from " + cr.getName() + " inventory");
        return true;
    }

    @Override
    public void add(Equipment e, Creature cr, double x, double y) {
        if (!inventory.add(e)) {
            Coords bottom = cr.getCenter();
            double dropX = bottom.x - e.getImageWidth()/2;
            double dropY = bottom.y - e.getImageHeight()/2;
            if (!e.onDrop(cr, dropX, dropY)) {
                cr.getItems().add(e);
            }
            System.out.println(e.getName() + " does not fit " + cr.getName() + " inventory");
        } else {
            e.setPos(x, y, null);
            System.out.println(e.getName() + " added to " + cr.getName() + " inventory");
        }
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }
}
