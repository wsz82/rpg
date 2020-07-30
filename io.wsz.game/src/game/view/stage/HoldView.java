package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class HoldView extends EquipmentView {
    private Inventory inventory;

    public HoldView(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawEquipment();
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
            Image img = e.getImage();
            double viewX = viewPos.x * meter;
            double viewY = viewPos.y * meter;
            double viewWidth = this.viewWidth * meter;
            double viewHeight = this.viewHeight * meter;

            cutImageAndDraw(x, y, img, viewX, viewY, viewWidth, viewHeight);
        }

        drawHoldSize();

        drawHoldWeight();
    }

    private void drawHoldSize() {
        double x = (viewPos.x - viewHeight/2) * Sizes.getMeter();
        double y = (viewPos.y + 1.0/5*viewHeight) * Sizes.getMeter();
        drawSize(inventory.getFilledSpace(), inventory.getMaxSize(), x, y);
    }

    private void drawHoldWeight() {
        double x = (viewPos.x - viewHeight/2) * Sizes.getMeter();
        double y = (viewPos.y + 4.0/5*viewHeight) * Sizes.getMeter();
        double weight = inventory.getActualWeight();
        double maxWeight = inventory.getMaxWeight();

        gc.setTextBaseline(VPos.TOP);
        gc.setTextAlign(TextAlignment.CENTER);

        Color actualWeightTextColor;
        if (weight > maxWeight) {
            actualWeightTextColor = Color.RED;
        } else if (weight > maxWeight/2) {
            actualWeightTextColor = Color.YELLOW;
        } else {
            actualWeightTextColor = Color.BLACK;
        }
        gc.setStroke(actualWeightTextColor);
        String filled = String.format("%.2f", weight);
        gc.strokeText(filled, x, y);

        gc.setStroke(Color.BLACK);
        y += gc.getFont().getSize();
        String max = String.format("%.2f", maxWeight);
        gc.strokeText(max, x, y);
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
