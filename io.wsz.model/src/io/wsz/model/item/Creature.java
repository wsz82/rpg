package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.animation.creature.CreatureAnimationPos;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Creature extends PosItem<Creature, CreatureAnimationPos> implements Containable {
    private static final long serialVersionUID = 1L;

    private final Coords centerBottom = new Coords();
    private final Coords reversedCenterBottom = new Coords();

    private CreatureAnimation animation;

    private final Task task = new Task();
    private final CreatureAnimationPos animationPos;
    private Inventory inventory;
    private CreatureSize size;
    private CreatureControl control;
    private Double speed;
    private Double visionRange;
    private Double range;
    private Integer strength;

    public Creature() {
        this.animationPos = new CreatureAnimationPos();
    }

    public Creature(ItemType type) {
        super(type);
        this.animation = new CreatureAnimation(getDir());
        this.animationPos = new CreatureAnimationPos();
        this.inventory = new Inventory(this);
    }

    public Creature(Creature prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new CreatureAnimationPos();
    }

    @Override
    public double getCollisionLeft(List<List<Coords>> cp) {
        return getCollisionLeft(cp, getCenter());
    }

    @Override
    public double getCollisionLeft(List<List<Coords>> cp, Coords nextPos) {
        double halfWidth = getSize().getWidth() / 2;
        return nextPos.x - halfWidth;
    }

    @Override
    public double getCollisionRight(List<List<Coords>> cp) {
        return getCollisionRight(cp, getCenter());
    }

    @Override
    public double getCollisionRight(List<List<Coords>> cp, Coords nextPos) {
        double halfWidth = getSize().getWidth() / 2;
        return nextPos.x + halfWidth;
    }

    @Override
    public double getCollisionTop(List<List<Coords>> cp) {
        return getCollisionTop(cp, getCenter());
    }

    @Override
    public double getCollisionTop(List<List<Coords>> cp, Coords nextPos) {
        double halfHeight = getSize().getHeight() / 2;
        return nextPos.y - halfHeight;
    }

    @Override
    public double getCollisionBottom(List<List<Coords>> cp) {
        return getCollisionBottom(cp, getCenter());
    }

    @Override
    public double getCollisionBottom(List<List<Coords>> cp, Coords nextPos) {
        double halfHeight = getSize().getHeight() / 2;
        return nextPos.y + halfHeight;
    }

    private void checkSurrounding() {
        Teleport t = getController().getBoard().getTeleport(getCenter(), this, this.pos.getLocation());
        if (t != null) t.enter(this);
    }

    @Override
    public Coords getCenter() {
        return getCenter(pos);
    }

    @Override
    protected Coords getCoordsForCollisionCheck() {
        return getCenter();
    }

    public Coords getCenter(Coords pos) {
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        centerBottom.x = pos.x + width/2;
        centerBottom.y = pos.y + height;
        centerBottom.level = pos.level;
        return centerBottom;
    }

    public Coords reverseCenterBottomPos(double x, double y, int level, Location location) {
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        reversedCenterBottom.x = x - width/2;
        reversedCenterBottom.y = y - height;
        reversedCenterBottom.level = level;
        reversedCenterBottom.setLocation(location);
        return reversedCenterBottom;
    }

    public double reverseCenterBottomPosX(double x) {
        double width = getImage().getWidth() / Sizes.getMeter();
        x -= width/2;
        return x;
    }

    public double reverseCenterBottomPosY(double y) {
        double height = getImage().getHeight() / Sizes.getMeter();
        y -= height;
        return y;
    }

    public Coords reverseCenterBottomPos(Coords difPos) {
        return reverseCenterBottomPos(difPos.x, difPos.y, difPos.level, difPos.getLocation());
    }

    public void onFirstAction(PosItem pi) {
        if (pi == null) {
            return;
        }
        setItemTask(pi);
    }

    public void goTo(double x, double y) {
        this.task.setFinished(false);
        this.task.setItem(this, null);
        this.task.setDestX(reverseCenterBottomPosX(x));
        this.task.setDestY(reverseCenterBottomPosY(y));
    }

    private void setItemTask(PosItem e) {
        this.task.setFinished(false);
        this.task.setItem(this, e);
    }

    @Override
    public boolean withinRange(Coords pos, double range, double sizeWidth, double sizeHeight) {
        Coords thisCenter = getCenter();

        CreatureSize thisSize = getSize();
        double width = sizeWidth + 2*range;
        return Geometry.ovalsIntersect(thisCenter, thisSize, pos, width);
    }

    public List<Equipment> getEquipmentWithinRange(Controller controller) {
        return getController().getBoard().getEquipmentWithinRange(this);
    }

    private void checkTask() {
        task.doTask(this);
    }

    @Override
    public double getLeft() {
        double width = getSize().getWidth();
        double imgWidth = getImageWidth();
        double imgLeft = pos.x;
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgLeft -= halfDif;
        }
        return imgLeft;
    }

    @Override
    public double getRight() {
        double width = getSize().getWidth();
        double imgWidth = getImageWidth();
        double imgRight = pos.x + imgWidth;
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgRight += halfDif;
        }
        return imgRight;
    }

    @Override
    public double getTop() {
        double halfHeight = getCreatureHalfHeight();
        double imgHeight = getImageHeight();
        double imgBottom = pos.y + imgHeight;
        double imgTop = pos.y;
        if (imgHeight < halfHeight) {
            imgTop = imgBottom - halfHeight;
        }
        return imgTop;
    }

    @Override
    public double getBottom() {
        double imgHeight = getImageHeight();
        double imgBottom = pos.y + imgHeight;
        return imgBottom;
    }

    private double getCreatureHalfHeight() {
        double height = getSize().getHeight();
        return height / 2;
    }

    public Task getTask() {
        return task;
    }

    public Inventory getIndividualInventory() {
        return inventory;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            if (isThisPrototype()) {
                return null;
            } else {
                return prototype.getInventory();
            }
        } else {
            return inventory;
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<InventoryPlaceType, InventoryPlace> getInventoryPlaces() {
        if (inventory == null) {
            if (isThisPrototype()) {
                return null;
            } else {
                return prototype.getInventoryPlaces();
            }
        } else {
            Map<InventoryPlaceType, InventoryPlace> inventoryPlaces = inventory.getInventoryPlaces();
            if (inventoryPlaces == null) {
                return prototype.getInventoryPlaces();
            } else {
                return inventoryPlaces;
            }
        }
    }

    public Double getIndividualSpeed() {
        return speed;
    }

    public Double getSpeed() {
        if (speed == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.speed;
        } else {
            return speed;
        }
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getIndividualRange() {
        return range;
    }

    public Double getRange() {
        if (range == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.range;
        } else {
            return range;
        }
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public Double getIndividualVisionRange() {
        return visionRange;
    }

    public Double getVisionRange() {
        if (visionRange == null) {
            if (isThisPrototype()) {
                return 1.0;
            }
            return prototype.visionRange;
        } else {
            return visionRange;
        }
    }

    public void setVisionRange(Double visionRange) {
        this.visionRange = visionRange;
    }

    public CreatureSize getIndividualSize() {
        return size;
    }

    public CreatureSize getSize() {
        if (size == null) {
            if (isThisPrototype()) {
                return CreatureSize.getDefault();
            }
            return prototype.size;
        } else {
            return size;
        }
    }

    public void setSize(CreatureSize size) {
        this.size = size;
    }

    public CreatureControl getIndividualControl() {
        return control;
    }

    public CreatureControl getControl() {
        if (control == null) {
            if (isThisPrototype()) {
                return CreatureControl.getDefault();
            }
            return prototype.getControl();
        } else {
            return control;
        }
    }

    public void setControl(CreatureControl control) {
        this.control = control;
    }

    public Integer getIndividualStrength() {
        return strength;
    }

    public Integer getStrength() {
        if (strength == null) {
            if (isThisPrototype()) {
                return 0;
            }
            return prototype.strength;
        } else {
            return strength;
        }
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public void onSecondAction(PosItem pi) {
        pi.creatureSecondaryInteract(this);
    }

    @Override
    public CreatureAnimation getAnimation() {
        if (isThisPrototype()) {
            return animation;
        } else {
            return prototype.getAnimation();
        }
    }

    @Override
    public CreatureAnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getControl() != CreatureControl.ENEMY) {
                if (getObstacleOnWay(cr) != null) return false;
                Controller controller = getController();
                controller.setAsking(cr);
                controller.setAnswering(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public void changeLocation(Location from, Coords exit) {
        super.changeLocation(from, exit);
        Coords reversed = reverseCenterBottomPos(exit);
        this.pos.x = reversed.x;
        this.pos.y = reversed.y;
        this.pos.level = reversed.level;
        task.clear();
    }

    @Override
    public void update() {
        super.update();
        checkSurrounding();
        checkTask();
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Creature creature = (Creature) o;
        return Objects.equals(getTask(), creature.getTask()) &&
                Objects.equals(getInventory(), creature.getInventory()) &&
                getSize() == creature.getSize() &&
                getControl() == creature.getControl() &&
                Objects.equals(getSpeed(), creature.getSpeed()) &&
                Objects.equals(getRange(), creature.getRange()) &&
                Objects.equals(getStrength(), creature.getStrength());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTask(), getInventory(), getSize(), getControl(), getSpeed(), getRange(), getStrength());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(task);

        out.writeObject(inventory);

        out.writeObject(size);

        out.writeObject(control);

        out.writeObject(speed);

        out.writeObject(visionRange);

        out.writeObject(range);

        out.writeObject(strength);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        if (isThisPrototype()) {
            animation = new CreatureAnimation(getDir());
        }

        Task readTask = (Task) in.readObject();
        task.setDest(readTask.getDest());
        task.setItem(this, readTask.getItem());

        inventory = (Inventory) in.readObject();

        size = (CreatureSize) in.readObject();

        control = (CreatureControl) in.readObject();

        speed = (Double) in.readObject();

        visionRange = (Double) in.readObject();

        range = (Double) in.readObject();

        strength = (Integer) in.readObject();
    }
}
