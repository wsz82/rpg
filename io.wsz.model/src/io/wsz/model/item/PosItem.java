package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PosItem<A extends PosItem> extends Asset implements ItemUpdater, Interactable {
    private static final long serialVersionUID = 1L;

    protected final Coords center = new Coords();

    protected final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");
    protected final Coords pos = new Coords();

    protected A prototype;
    protected List<Coords> coverLine;
    protected List<List<Coords>> collisionPolygons;
    protected Dialog dialog;
    protected Coords interactionCoords;

    public PosItem() {}

    public PosItem(ItemType type) {
        super(type);
        this.visible.set(true);
        this.coverLine = new ArrayList<>(0);
        this.collisionPolygons = new ArrayList<>(0);
        this.dialog = new Dialog();
    }

    public PosItem(A prototype, Boolean visible) {
        this.prototype = prototype;
        this.visible.set(visible);
    }

    public Coords getImageCenter() {
        center.x = pos.x + getImageWidth()/2;
        center.y = pos.y + getImageHeight()/2;
        center.level = pos.level;
        return center;
    }

    public Coords getCenter() {
        center.x = pos.x + getImageWidth()/2;
        center.y = pos.y + getImageHeight()/2;
        center.level = pos.level;
        return center;
    }

    public double getLeft() {
        return pos.x;
    }

    public double getTop() {
        return pos.y;
    }

    public double getRight() {
        return pos.x + getImageWidth();
    }

    public double getBottom() {
        return pos.y + getImageHeight();
    }

    public void changeLocation(Location from, Coords exit) {
        Location target = exit.getLocation();
        if (!from.equals(target)) {
            from.getItemsToRemove().add(this);
            target.getItemsToAdd().add(this);
        }

        pos.x = exit.x;
        pos.y = exit.y;
        pos.level = exit.level;
    }

    public boolean withinRange(Coords pos, double range, double sizeWidth, double sizeHeight) {
        return Coords.pointWithinOval(getInteractionCoords(), pos, sizeWidth + 2*range, sizeHeight + 2*range);
    }

    public PosItem getCollision() {
        return getCollision(getCoordsForCollisionCheck());
    }

    protected Coords getCoordsForCollisionCheck() {
        return pos;
    }

    public PosItem getCollision(Coords nextPos) {
        return Controller.get().getBoard().getObstacle(nextPos, this, pos.getLocation());
    }

    public double getCollisionLeft(List<List<Coords>> cp) {
        return getCollisionLeft(cp, pos);
    }

    public double getCollisionLeft(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.x;
        double left =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.x)
                        .min()
                        .getAsDouble())
                .min()
                .getAsDouble();
        return nextPos.x + left;
    }

    public double getCollisionRight(List<List<Coords>> cp) {
        return getCollisionRight(cp, pos);
    }

    public double getCollisionRight(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.x;
        double right =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.x)
                        .max()
                        .getAsDouble())
                .max()
                .getAsDouble();
        return nextPos.x + right;
    }

    public double getCollisionTop(List<List<Coords>> cp) {
        return getCollisionTop(cp, pos);
    }

    public double getCollisionTop(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.y;
        double top =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.y)
                        .min()
                        .getAsDouble())
                .min()
                .getAsDouble();
        return nextPos.y + top;
    }

    public double getCollisionBottom(List<List<Coords>> cp) {
        return getCollisionBottom(cp, pos);
    }

    public double getCollisionBottom(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.y;
        double bottom = cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.y)
                        .max()
                        .getAsDouble())
                .max()
                .getAsDouble();
        return nextPos.y + bottom;
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
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.level = pos.level;
        this.pos.setLocation(pos.getLocation());
    }

    public void setPos(double x, double y, Location location) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.setLocation(location);
    }

    public Coords getIndividualInteractionCoords() {
        return interactionCoords;
    }

    public Coords getInteractionCoords() {
        if (interactionCoords == null) {
            if (prototype != null) {
                if (prototype.interactionCoords != null) {
                    center.x = prototype.interactionCoords.x;
                    center.y = prototype.interactionCoords.y;
                } else {
                    return getCenter();
                }
            } else {
                return getCenter();
            }
        } else {
            center.x = interactionCoords.x;
            center.y = interactionCoords.y;
        }
        center.add(pos);
        return center;
    }

    public void setInteractionCoords(Coords interactionCoords) {
        this.interactionCoords = interactionCoords;
    }

    public List<Coords> getCoverLine() {
        if (coverLine == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.coverLine;
        } else {
            return coverLine;
        }
    }

    public List<Coords> getActualCoverLine() {
        return getCoverLine();
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
    }

    public List<List<Coords>> getCollisionPolygons() {
        if (collisionPolygons == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.collisionPolygons;
        } else {
            return collisionPolygons;
        }
    }

    public List<List<Coords>> getActualCollisionPolygons() {
        return getCollisionPolygons();
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
    public ItemType getType() {
        if (prototype != null) {
            return prototype.getType();
        }
        return super.getType();
    }

    @Override
    public String getRelativePath() {
        if (prototype != null) {
            return prototype.getRelativePath();
        }
        return super.getRelativePath();
    }

    @Override
    public Image getImage() {
        if (prototype == null) {
            return super.getImage();
        } else {
            return prototype.getImage();
        }
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return getObstacleOnWay(cr) == null;
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        return getObstacleOnWay(cr) == null;
    }

    protected PosItem getObstacleOnWay(Creature cr) {
        Coords crCenter = cr.getCenter();
        double xFrom = crCenter.x;
        double yFrom = crCenter.y;
        Coords toCoords = getInteractionCoords();
        double xTo = toCoords.x;
        double yTo = toCoords.y;
        return Controller.get().getBoard().getObstacleOnWay(
                pos.getLocation(), pos.level, xFrom, yFrom, this, xTo, yTo);
    }

    @Override
    public void update() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosItem)) return false;
        if (!super.equals(o)) return false;
        PosItem<?> item = (PosItem<?>) o;
        return Objects.equals(getPrototype(), item.getPrototype()) &&
                Objects.equals(getVisible(), item.getVisible()) &&
                Objects.equals(getPos(), item.getPos()) &&
                Objects.equals(getCoverLine(), item.getCoverLine()) &&
                Objects.equals(getCollisionPolygons(), item.getCollisionPolygons()) &&
                Objects.equals(getDialog(), item.getDialog());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPrototype(), getVisible(), getPos(),
                getCoverLine(), getCollisionPolygons(), getDialog());
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(prototype);

        out.writeBoolean(visible.get());

        out.writeObject(pos);

        out.writeObject(coverLine);

        out.writeObject(collisionPolygons);

        out.writeObject(dialog);

        out.writeObject(interactionCoords);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        prototype = (A) in.readObject();

        visible.set(in.readBoolean());

        Coords pos = (Coords) in.readObject();
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.level = pos.level;
        this.pos.setLocation(pos.getLocation());

        coverLine = (List<Coords>) in.readObject();

        collisionPolygons = (List<List<Coords>>) in.readObject();

        dialog = (Dialog) in.readObject();

        interactionCoords = (Coords) in.readObject();
    }
}
