package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class DropView extends EquipmentView {
    private final Coords creaturePos = new Coords();
    private final List<PosItem> allItmes = new ArrayList<>(0);

    private List<Equipment> droppedEquipment;

    public DropView(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void refresh() {
        super.refresh();

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
        gc.restore();
    }

    @Override
    protected final void drawEquipment() {

        sortItems();

        for (PosItem pi : allItmes) {
            Creature cr = controller.getCreatureToOpenInventory();

            adjustCoverOpacity(cr, pi);

            if (pi == cr) {
                drawCreatureSize();
            }

            Coords pos = pi.getPos();
            currentPosCorrection(pos);
            Coords corrected = modifiedCoords;
            double x = corrected.x * Sizes.getMeter();
            double y = corrected.y * Sizes.getMeter();

            Image img = pi.getImage();
            double width = img.getWidth();
            double height = img.getHeight();
            double viewX = viewPos.x * Sizes.getMeter();
            double viewY = viewPos.y * Sizes.getMeter();
            double viewWidth = this.viewWidth * Sizes.getMeter();
            double viewHeight = this.viewHeight * Sizes.getMeter();

            double startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            if (width > viewWidth) {
                width = viewWidth;
            }

            double startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }
            if (height > viewHeight) {
                height = viewHeight;
            }

            double destX = 0;
            if (x > 0) {
                destX = x;
            }
            double destY = 0;
            if (y > 0) {
                destY = y;
            }
            gc.drawImage(img, startX, startY, width, height, destX + viewX, destY + viewY, width, height);
            gc.setGlobalAlpha(1.0);
        }
    }

    private Coords currentPosCorrection(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(currentPos);
        return modifiedCoords;
    }

    private void drawCreatureSize() {
        Creature cr = controller.getCreatureToOpenInventory();
        CreatureSize size = cr.getSize();

        Coords pos = cr.getCenter();
        Coords corrected = currentPosCorrection(pos);
        double x = (corrected.x + viewPos.x) * Sizes.getMeter();
        double y = (corrected.y + viewPos.y) * Sizes.getMeter();

        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0 * Sizes.getMeter(), y - size.getHeight()/2.0 * Sizes.getMeter(),
                size.getWidth() * Sizes.getMeter(), size.getHeight() * Sizes.getMeter());
    }

    private void sortItems() {
        Creature cr = controller.getCreatureToOpenInventory();
        Location loc = cr.getPos().getLocation();

        sortItems(loc, currentPos, viewWidth, viewHeight, allItmes, cr.getLevel());
    }

    public void setDroppedEquipment(List<Equipment> droppedEquipment) {
        this.droppedEquipment = droppedEquipment;
    }

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BROWN);
        gc.fillOval(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    public void setCreaturePos(Coords pos) {
        this.creaturePos.x = pos.x;
        this.creaturePos.y = pos.y;
    }

    @Override
    public void remove(Equipment e, Creature cr) {
        e.onTake(cr, 0, 0);
        droppedEquipment.remove(e);
    }

    @Override
    public void add(Equipment e, Creature cr, double x, double y) {
        if (!e.onDrop(cr, x, y)) {
            cr.getItems().add(e);
        }
    }

    @Override
    public List<Equipment> getItems() {
        return droppedEquipment;
    }
}