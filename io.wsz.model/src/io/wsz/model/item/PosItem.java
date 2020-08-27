package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class PosItem<A extends PosItem, B extends AnimationPos> extends Asset implements Updatable, Interactable, Animable {
    private static final long serialVersionUID = 1L;

    protected final Coords center = new Coords();

    protected Controller controller;

    protected BooleanProperty visible;
    protected Coords pos;
    protected A prototype;
    protected List<Coords> coverLine;
    protected List<List<Coords>> collisionPolygons;
    protected Dialog dialog;
    protected Coords interactionCoords;
    protected Double animationSpeed;
    protected ResolutionImage image;

    public PosItem() {
        this.visible = new SimpleBooleanProperty(this, "visible");
        this.pos = new Coords();
    }

    public PosItem(ItemType type) {
        super(type);
        this.visible = new SimpleBooleanProperty(this, "visible", true);
        this.pos = new Coords();
        this.coverLine = new ArrayList<>(0);
        this.collisionPolygons = new ArrayList<>(0);
        this.dialog = new Dialog();
    }

    public PosItem(A prototype, Boolean visible) {
        this.prototype = prototype;
        this.visible = new SimpleBooleanProperty(this, "visible", visible);
        this.pos = new Coords();
    }

    public PosItem(PosItem<A, B> other) {
        super(other);
        this.visible = new SimpleBooleanProperty(this, "visible", other.visible.get());
        this.pos = other.pos.clonePos();
        this.prototype = other.prototype;
        this.coverLine = Geometry.cloneCoordsList(other.coverLine);
        this.collisionPolygons = Geometry.cloneCoordsPolygons(other.collisionPolygons);
        this.dialog = other.dialog;
        Coords interactionCoords = other.interactionCoords;
        if (interactionCoords == null) {
            this.interactionCoords = null;
        } else {
            this.interactionCoords = interactionCoords.clonePos();
        }
        this.image = other.image;
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
        return Geometry.pointWithinOval(getInteractionCoords(), pos, sizeWidth + 2*range, sizeHeight + 2*range);
    }

    public PosItem getCollision() {
        return getCollision(getCoordsForCollisionCheck());
    }

    protected Coords getCoordsForCollisionCheck() {
        return pos;
    }

    public PosItem getCollision(Coords nextPos) {
        return getCollision(nextPos, pos.getLocation());
    }

    public PosItem getCollision(Coords nextPos, Location nextLocation) {
        return getController().getBoard().getObstacle(nextPos, this, nextLocation);
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
            } else {
                return prototype.dialog;
            }
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

    protected Controller getController() {
        if (prototype == null) {
            return controller;
        } else {
            return prototype.getController();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
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
    public String getPath() {
        if (prototype != null) {
            return prototype.getPath();
        }
        return super.getPath();
    }

    public Double getIndividualAnimationSpeed() {
        return animationSpeed;
    }

    public Double getAnimationSpeed() {
        if (animationSpeed == null) {
            if (prototype == null) {
                return 1.0;
            } else {
                return prototype.getAnimationSpeed();
            }
        } else {
            return animationSpeed;
        }
    }

    public void setAnimationSpeed(Double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public final ResolutionImage getInitialImage() {
        if (image == null) {
            File programDir = getController().getProgramDir();
            image = getAnimation().getBasicMain(programDir);
        }
        return image;
    }

    public ResolutionImage getImage() {
        if (image == null) {
            if (prototype == null) {
                File programDir = getController().getProgramDir();
                image = getAnimation().getBasicMain(programDir);
                return image;
            } else {
                return prototype.getImage();
            }
        } else {
            return image;
        }
    }

    public double getImageHeight() {
        ResolutionImage img = getImage();
        if (img == null) return 0;
        return img.getHeight() / Sizes.getMeter();
    }

    public double getImageWidth() {
        ResolutionImage img = getImage();
        if (img == null) return 0;
        return img.getWidth() / Sizes.getMeter();
    }

    public void setImage(ResolutionImage image) {
        this.image = image;
    }

    protected boolean isThisPrototype() {
        return prototype == null;
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
        return getController().getBoard().getObstacleOnWay(
                pos.getLocation(), pos.level, xFrom, yFrom, this, xTo, yTo);
    }

    @Override
    public void update() {
        playAnimation();
    }

    private void playAnimation() {
        Animation animation = getAnimation();
        if (animation == null) return;
        animation.play(this);
    }

    public abstract B getAnimationPos();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosItem)) return false;
        if (!super.equals(o)) return false;
        PosItem<?, ?> posItem = (PosItem<?, ?>) o;
        return Objects.equals(getVisible(), posItem.getVisible()) &&
                Objects.equals(getPos(), posItem.getPos()) &&
                Objects.equals(getPrototype(), posItem.getPrototype()) &&
                Objects.equals(getCoverLine(), posItem.getCoverLine()) &&
                Objects.equals(getCollisionPolygons(), posItem.getCollisionPolygons()) &&
                Objects.equals(getDialog(), posItem.getDialog()) &&
                Objects.equals(getInteractionCoords(), posItem.getInteractionCoords()) &&
                Objects.equals(getAnimationSpeed(), posItem.getAnimationSpeed()) &&
                Objects.equals(getImage(), posItem.getImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getVisible(), getPos(), getPrototype(), getCoverLine(), getCollisionPolygons(), getDialog(), getInteractionCoords(), getAnimationSpeed(), getImage());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(prototype);

        out.writeObject(animationSpeed);

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

        animationSpeed = (Double) in.readObject();

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
