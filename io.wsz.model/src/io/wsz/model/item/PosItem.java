package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public abstract class PosItem<A extends PosItem> extends Asset implements ItemUpdater {
    private static final long serialVersionUID = 1L;

    protected A prototype;
    protected final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");
    protected Coords pos;
    protected Integer level;
    protected List<Coords> coverLine;
    protected List<List<Coords>> collisionPolygons;
    protected Dialog dialog;

    public PosItem() {}

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

    public Coords getCenter() {
        double centerX = pos.x + getImage().getWidth()/ Sizes.getMeter();
        double centerY = pos.y + getImage().getHeight()/ Sizes.getMeter();
        return new Coords(centerX, centerY);
    }

    public void centerScreenOn(Coords targetPos) {
        Controller.get().setPosToCenter(targetPos);
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

    public Dialog getIndividualDialog() {
        return dialog;
    }

    public Dialog getDialog() {
        if (dialog == null) {
            if (prototype == null) {
                return null;
            }
            return prototype.dialog;
        } else {
            return dialog;
        }
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public A getPrototype() {
        return prototype;
    }

    public void setPrototype(A prototype) {
        this.prototype = prototype;
    }

    @Override
    public String getName() {
        if (prototype != null) {
            return prototype.getName();
        }
        return super.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosItem)) return false;
        if (!super.equals(o)) return false;
        PosItem<?> item = (PosItem<?>) o;
        return Objects.equals(getPrototype(), item.getPrototype()) &&
                Objects.equals(getVisible(), item.getVisible()) &&
                Objects.equals(getPos(), item.getPos()) &&
                Objects.equals(getLevel(), item.getLevel()) &&
                Objects.equals(getCoverLine(), item.getCoverLine()) &&
                Objects.equals(getCollisionPolygons(), item.getCollisionPolygons()) &&
                Objects.equals(getDialog(), item.getDialog());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPrototype(), getVisible(), getPos(), getLevel(),
                getCoverLine(), getCollisionPolygons(), getDialog());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(prototype);

        out.writeBoolean(visible.get());

        out.writeObject(pos);

        out.writeObject(level);

        out.writeObject(coverLine);

        out.writeObject(collisionPolygons);

        out.writeObject(dialog);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        prototype = (A) in.readObject();

        visible.set(in.readBoolean());

        pos = (Coords) in.readObject();

        level = (Integer) in.readObject();

        coverLine = (List<Coords>) in.readObject();

        collisionPolygons = (List<List<Coords>>) in.readObject();

        dialog = (Dialog) in.readObject();
    }
}
