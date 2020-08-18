package game.view.stage;

import game.model.GameController;
import io.wsz.model.item.*;
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
    private double visionWidthDiameter;
    private double visionHeightDiameter;
    private double minCurPosY;
    private double minCurPosX;
    private double maxCurPosX;
    private double xScrollPos;
    private double xScrollButtonWidth;
    private boolean xScrollVisible;

    public DropView(Canvas canvas, GameController gameController) {
        super(canvas, gameController);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawHorScroll();

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

    private void drawHorScroll() {
        xScrollVisible = !(visionWidthDiameter <= viewWidth);
        if (!xScrollVisible) {
            return;
        }
        int meter = Sizes.getMeter();
        double y = (viewPos.y + viewHeight) * meter;

        clearHorScroll(y);
        drawHorScrollButton(y);
    }

    private void drawHorScrollButton(double y) {
        minCurPosX = creaturePos.x - visionWidthDiameter/2;
        maxCurPosX = creaturePos.x + visionWidthDiameter/2;
        xScrollPos = (curPos.x - minCurPosX) * viewWidth / visionWidthDiameter;
        double x = viewPos.x + xScrollPos;
        xScrollButtonWidth = viewWidth * viewWidth / visionWidthDiameter;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x * meter, y, xScrollButtonWidth * meter, scrollWidth * meter);
    }

    private void clearHorScroll(double y) {
        gc.setFill(Color.BLUE);
        int meter = Sizes.getMeter();
        gc.fillRect(viewPos.x * meter, y, viewWidth * meter, scrollWidth * meter);
    }

    @Override
    protected final void drawEquipment() {

        sortItems();

        for (PosItem pi : allItmes) {
            Creature cr = controller.getCreatureToOpenInventory();

            adjustCoverOpacity(cr, pi);

            if (pi == cr) {
                drawCreatureBase(cr);
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
        yScrollVisible = !(visionHeightDiameter <= viewHeight);
        if (!yScrollVisible) {
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
        maxCurPosY = creaturePos.y + visionHeightDiameter/2;
        yScrollPos = (curPos.y - minCurPosY) * viewHeight / visionHeightDiameter;
        double y = viewPos.y + yScrollPos;
        yScrollButtonHeight = viewHeight * viewHeight / visionHeightDiameter;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x, y * meter, scrollWidth * meter, yScrollButtonHeight * meter);
    }

    private void drawCreatureBase(Creature cr) {
        CreatureSize size = cr.getSize();
        CreatureControl control = cr.getControl();
        Coords pos = cr.getCenter();
        Coords corrected = currentPosCorrection(pos);
        double x = (corrected.x + viewPos.x);
        double y = (corrected.y + viewPos.y);
        drawCreatureBase(x, y, size, control);
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
        gc.setFill(Color.DARKGRAY);
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

    public double getXScrollPos() {
        return xScrollPos;
    }

    public double getXScrollButtonWidth() {
        return xScrollButtonWidth;
    }

    public void setScrollPosX(double x) {
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

    public boolean isXScrollVisible() {
        return xScrollVisible;
    }

    public double getMinCurPosY() {
        return minCurPosY;
    }
}