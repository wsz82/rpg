package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public abstract class EquipmentView {
    protected final GraphicsContext gc;
    protected final Coords modifiedCoords = new Coords();
    protected final Coords viewPos = new Coords();
    protected final Coords currentPos = new Coords();

    protected List<Equipment> equipment;
    protected double viewWidth;
    protected double viewHeight;

    public EquipmentView(GraphicsContext gc) {
        this.gc = gc;
    }

    public final void refresh() {
        if (viewWidth < 0 || viewHeight < 0) {
            return;
        }
        drawBackground();

        drawEquipment();
    }

    protected abstract void drawEquipment();

    protected abstract void drawBackground();

    protected Coords translateCoordsToScreenCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(currentPos);
        modifiedCoords.add(viewPos);
        return modifiedCoords;
    }

    public Coords getLocalCoords(Coords pos) {
        modifiedCoords.x = pos.x;
        modifiedCoords.y = pos.y;
        modifiedCoords.subtract(viewPos);
        modifiedCoords.add(currentPos);
        return modifiedCoords;
    }

    public void setSize(double width, double height) {
        this.viewWidth = width;
        this.viewHeight = height;
    }

    public void setItems(List<Equipment> hold) {
        equipment = hold;
    }

    public List<Equipment> getItems() {
        return equipment;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    public void setCurrentPos(double x, double y) {
        currentPos.x = x;
        currentPos.y = y;
    }

    public Coords getCurrentPos() {
        return currentPos;
    }

    public void setViewPos(double x, double y) {
        viewPos.x = x;
        viewPos.y = y;
    }

    public Coords getViewPos() {
        return viewPos;
    }

    public abstract void remove(Equipment e, Creature cr);

    public abstract void add(Equipment e, Creature cr, double x, double y);

    public Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment e) {
        if (mousePos.x < viewPos.x) {
            mousePos.x = currentPos.x;
        } else {
            double width = viewWidth - e.getImageWidth();
            if (mousePos.x > viewPos.x + width) {
                mousePos.x = currentPos.x + width;
            } else {
                mousePos.x = mousePos.x - viewPos.x + currentPos.x - draggedCoords.x;
            }
        }
        if (mousePos.y < viewPos.y) {
            mousePos.y = currentPos.y;
        } else {
            double height = viewHeight - e.getImageHeight();
            if (mousePos.y > viewPos.y + height) {
                mousePos.y = currentPos.y + height;
            } else {
                mousePos.y = mousePos.y - viewPos.y + currentPos.y - draggedCoords.y;
            }
        }
        return mousePos;
    }
}
