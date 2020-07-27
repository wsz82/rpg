package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.stage.Coords;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class EquipmentView extends CanvasView {
    protected final List<Equipment> items = new ArrayList<>(0);
    protected final Coords modifiedCoords = new Coords();
    protected final Coords viewPos = new Coords();
    protected final Coords curPos = new Coords();

    protected double viewWidth;
    protected double viewHeight;

    public EquipmentView(Canvas canvas) {
        super(canvas);
    }

    public void refresh() {
        if (viewWidth < 0 || viewHeight < 0) {
            return;
        }
        drawBackground();
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

    protected abstract void drawBackground();

    protected Coords translateCoordsToScreenCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(curPos);
        modifiedCoords.add(viewPos);
        return modifiedCoords;
    }

    public Coords getLocalCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(viewPos);
        modifiedCoords.add(curPos);
        return modifiedCoords;
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

    public void setCurrentPos(double x, double y) {
        curPos.x = x;
        curPos.y = y;
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

    protected void drawSize(int filledSpace, int maxSize, double x, double y) {
        gc.setTextBaseline(VPos.TOP);
        gc.setTextAlign(TextAlignment.CENTER);

        Color filledSpaceTextColor;
        if (filledSpace >= maxSize) {
            filledSpaceTextColor = Color.RED;
        } else {
            filledSpaceTextColor = Color.BLACK;
        }
        gc.setStroke(filledSpaceTextColor);
        String filled = String.valueOf(filledSpace);
        gc.strokeText(filled, x, y);

        gc.setStroke(Color.BLACK);
        y += gc.getFont().getSize();
        String max = String.valueOf(maxSize);
        gc.strokeText(max, x, y);
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
}
