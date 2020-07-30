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
    private final List<PosItem> allItmes = new ArrayList<>(0);
    private final Coords creaturePos = new Coords();

    private List<Equipment> droppedEquipment;
    private double visionHeightDiameter;
    private double minCurPosY;

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
            Coords corrected = currentPosCorrection(pos);
            int meter = Sizes.getMeter();
            double x = corrected.x * meter;
            double y = corrected.y * meter;

            Image img = pi.getImage();
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
        if (visionHeightDiameter <= viewHeight) return;
        int meter = Sizes.getMeter();
        double x = (viewPos.x + viewWidth) * meter;

        clearVerScroll(x);
        drawVerScrollButton(x);
    }

    private void drawVerScrollButton(double x) {
        minCurPosY = creaturePos.y - visionHeightDiameter/2;
        maxCurPosY = creaturePos.y + visionHeightDiameter/2;
        yScrollPos = (curPos.y - minCurPosY) * viewHeight / visionHeightDiameter;
        double y = viewPos.y + yScrollPos;
        yScrollButtonHeight = viewHeight * viewHeight / visionHeightDiameter;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x, y * meter, scrollWidth * meter, yScrollButtonHeight * meter);
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

        sortItems(loc, curPos.x, curPos.y, viewWidth, viewHeight, allItmes, cr.getPos().level);
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

    @Override
    public boolean remove(Equipment e, Creature cr) {
        if (e.onTake(cr, 0, 0)) {
            droppedEquipment.remove(e);
            return true;
        }
        return false;
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

    @Override
    public void setScrollPosY(double y) {
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
}