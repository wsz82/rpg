package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public abstract class EquipmentView extends CanvasView {
    protected static final double SIZE_COLUMN_WIDTH = 0.01;
    protected static final double WEIGHT_COLUMN_WIDTH = 0.01;

    protected final List<Equipment> items = new ArrayList<>(0);
    protected final Coords modifiedCoords = new Coords();
    protected final Coords viewPos = new Coords();
    protected final Coords curPos = new Coords();

    protected double inventoryWidth;
    protected double viewWidth;
    protected double viewHeight;
    protected double scrollWidth;
    protected double yScrollPos;
    protected double yScrollButtonHeight;
    protected double maxCurPosY;
    protected Equipment dragged;
    protected boolean yScrollVisible = true;

    public EquipmentView(Canvas canvas) {
        super(canvas);
    }

    public void refresh() {
        if (viewWidth < 0 || viewHeight < 0) {
            return;
        }
        drawBackground();

        drawVerScroll();
    }

    protected void drawVerScroll() {
        int meter = Sizes.getMeter();
        double x = (viewPos.x + viewWidth) * meter;

        clearVerScroll(x);
        drawVerScrollButton(x);
    }

    protected void drawVerScrollButton(double x) {
        OptionalDouble optMaxHeight = getItems().stream()
                .mapToDouble(i -> i.getPos().y)
                .max();
        if (dragged != null) {
            maxCurPosY = Math.max(optMaxHeight.orElse(0), dragged.getPos().y);
            maxCurPosY = Math.max(viewHeight, maxCurPosY);
        } else {
            maxCurPosY = Math.max(viewHeight, optMaxHeight.orElse(0));
        }
        maxCurPosY += 1;
        yScrollPos = curPos.y * viewHeight / maxCurPosY;
        yScrollButtonHeight = viewHeight * viewHeight / maxCurPosY;
        if (yScrollPos + yScrollButtonHeight > viewHeight) {
            yScrollPos = viewHeight - yScrollButtonHeight;
        }
        double y = viewPos.y + yScrollPos;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x, y * meter, scrollWidth * meter, yScrollButtonHeight * meter);
    }

    protected void clearVerScroll(double x) {
        gc.setFill(Color.BLUE);
        int meter = Sizes.getMeter();
        gc.fillRect(x, viewPos.y * meter, scrollWidth * meter, viewHeight * meter);
    }

    protected abstract void drawEquipment();

    protected void selectEquipment() {
        double left = curPos.x;
        double right = left + viewWidth;
        double top = curPos.y;
        double bottom = top + viewHeight;

        items.clear();
        getItems().stream()
                .filter(e -> {
                    double eLeft = e.getLeft();
                    double eRight = e.getRight();
                    double eTop = e.getTop();
                    double eBottom = e.getBottom();
                    return Coords.doOverlap(
                            left, top, right, bottom,
                            eLeft, eTop, eRight, eBottom);
                })
                .collect(Collectors.toCollection(() -> items));
    }

    protected Coords currentPosCorrection(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(curPos);
        return modifiedCoords;
    }

    protected abstract void drawBackground();

    public Coords getLocalCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(viewPos);
        modifiedCoords.add(curPos);
        return modifiedCoords;
    }

    protected void drawColumn(double filled, double max, double columnWidth, double columnHeight,
                              double columnX, double columnY, Color backgroundColor) {
        drawColumn(columnWidth, columnHeight, columnX, columnY, backgroundColor);
        double columnFilledHeight = columnHeight * filled / max;
        columnFilledHeight = Math.min(columnFilledHeight, columnHeight);
        double columnFilledY = columnY + columnHeight - columnFilledHeight;
        Color filledColor;
        if (filled < 1.0/3 * max) {
            filledColor = Color.GREEN;
        } else if (filled >= 1.0/3 * max && filled < 2.0/3 * max) {
            filledColor = Color.YELLOW;
        } else {
            filledColor = Color.RED;
        }
        drawColumn(columnWidth, columnFilledHeight, columnX, columnFilledY, filledColor);
    }

    private void drawColumn(double columnWidth, double columnHeight, double columnX, double columnY,
                            Color backgroundColor) {
        gc.setFill(backgroundColor);
        int meter = Sizes.getMeter();
        gc.fillRect(columnX * meter, columnY * meter, columnWidth * meter, columnHeight * meter);
    }

    public abstract boolean remove(Equipment e, Creature cr);

    public abstract void add(Equipment e, Creature cr, double x, double y);

    public abstract List<Equipment> getItems();

    public Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment e) {
        if (mousePos.x < viewPos.x) {
            mousePos.x = curPos.x;
        } else {
            double width = viewWidth - e.getImageWidth();
            if (mousePos.x > viewPos.x + width) {
                mousePos.x = curPos.x + width;
            } else {
                mousePos.x = mousePos.x - viewPos.x + curPos.x - draggedCoords.x;
            }
        }
        if (mousePos.y < viewPos.y) {
            mousePos.y = curPos.y;
        } else {
            double height = viewHeight - e.getImageHeight();
            if (mousePos.y > viewPos.y + height) {
                mousePos.y = curPos.y + height;
            } else {
                mousePos.y = mousePos.y - viewPos.y + curPos.y - draggedCoords.y;
            }
        }
        return mousePos;
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

    public void setCurPosY(double y) {
        y -= yScrollButtonHeight/2;
        double maxY;
        if (y < 0) {
            curPos.y = 0;
        } else if (y > (maxY = viewHeight - yScrollButtonHeight)) {
            curPos.y = maxY / viewHeight * maxCurPosY;
        } else {
            curPos.y = y / viewHeight * maxCurPosY;
        }
    }

    public Coords getCurPos() {
        return curPos;
    }

    public void setViewPos(double x, double y) {
        viewPos.x = x;
        viewPos.y = y;
    }

    public Coords getViewPos() {
        return viewPos;
    }

    public double getScrollWidth() {
        return scrollWidth;
    }

    public void setScrollWidth(double scrollWidth) {
        this.scrollWidth = scrollWidth;
    }

    public double getYScrollPos() {
        return yScrollPos;
    }

    public double getYScrollButtonHeight() {
        return yScrollButtonHeight;
    }

    public void setDragged(Equipment e) {
        dragged = e;
    }

    public boolean isYScrollVisible() {
        return yScrollVisible;
    }

    public double getMaxCurPosY() {
        return maxCurPosY;
    }

    public double getMinCurPosY() {
        return 0;
    }

    public double getInventoryWidth() {
        return inventoryWidth;
    }

    public void setInventoryWidth(double inventoryWidth) {
        this.inventoryWidth = inventoryWidth;
    }
}
