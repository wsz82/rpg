package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class HoldView extends EquipmentView {
    private Inventory inventory;

    public HoldView(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawEquipment();
    }

    @Override
    protected final void drawEquipment() {

        selectItems();

        for (Equipment e : items) {
            final Coords pos = e.getPos();
            Coords screenCoords = translateCoordsToScreenCoords(pos);
            final double x = (screenCoords.x * Sizes.getMeter());
            final double y = (screenCoords.y * Sizes.getMeter());

            Image img = e.getImage();
            double width = img.getWidth();
            double height = img.getHeight();

            double startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            if (width > viewWidth * Sizes.getMeter()) {
                width = viewWidth * Sizes.getMeter();
            }

            double startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }
            if (height > viewHeight * Sizes.getMeter()) {
                height = viewHeight * Sizes.getMeter();
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
    public void remove(Equipment e, Creature cr) {
        inventory.remove(e);
    }

    @Override
    public boolean add(Equipment e, Creature cr, double x, double y) {
        e.setPos(x, y, null);
        if (!inventory.add(e)) {
            Coords bottom = cr.getCenter();
            double dropX = bottom.x - e.getImageWidth()/2;
            double dropY = bottom.y - e.getImageHeight()/2;
            if (!e.onDrop(cr, dropX, dropY)) {
                cr.getItems().add(e);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }
}
