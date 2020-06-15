package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;
import java.util.Objects;

public abstract class PosItem<A extends PosItem> extends Asset implements ItemUpdater {
    protected volatile A prototype;
    protected final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");
    protected volatile Coords pos;
    protected volatile Integer level;
    protected volatile List<Coords> coverLine;
    protected volatile List<List<Coords>> collisionPolygons;

    public PosItem(A prototype, String name, ItemType type, String path,
                   Boolean visible, Coords pos, Integer level,
                   List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path);
        this.prototype = prototype;
        this.visible.set(visible);
        this.pos = pos;
        this.level = level;
        this.coverLine = coverLine;
        this.collisionPolygons = collisionPolygons;
    }

    public void centerScreenOn(Coords targetPos) {
        Controller.get().setCenterPos(targetPos);
    }

    public void changeLocation(Location from, Location target, Layer targetLayer, double targetX, double targetY) {
        if (!from.equals(target)) {
            from.getItemsToRemove().add(this);
            target.getItemsToAdd().add(this);
        }

        pos.x = targetX;
        pos.y = targetY;
        level = targetLayer.getLevel();
    }

    public Boolean getVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible.set(visible);
    }

    public Coords getPos() {
        return pos;
    }

    public void setPos(Coords pos) {
        this.pos = pos;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Coords> getCoverLine() {
        if (prototype != null) {
            return prototype.coverLine;
        } else {
            return coverLine;
        }
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
    }

    public List<List<Coords>> getCollisionPolygons() {
        if (prototype != null) {
            return prototype.collisionPolygons;
        } else {
            return collisionPolygons;
        }
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
    }

    public A getPrototype() {
        return prototype;
    }

    public void setPrototype(A prototype) {
        this.prototype = prototype;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosItem<?> item = (PosItem<?>) o;
        return Objects.equals(prototype, item.prototype) &&
                Objects.equals(visible, item.visible) &&
                Objects.equals(pos, item.pos) &&
                Objects.equals(level, item.level) &&
                Objects.equals(coverLine, item.coverLine) &&
                Objects.equals(collisionPolygons, item.collisionPolygons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prototype, visible, pos, level, coverLine, collisionPolygons);
    }

    @Override
    public String toString() {
        return getName();
    }
}
