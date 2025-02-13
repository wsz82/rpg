package io.wsz.model.item;

import io.wsz.model.animation.creature.CreatureAnimationType;
import io.wsz.model.animation.creature.CreatureBaseAnimationType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import static io.wsz.model.sizes.Sizes.SECOND;

public class Task implements Externalizable {
    private static final long serialVersionUID = 1L;

    private final Coords nextPos = new Coords(-1, -1);

    private Coords dest;
    private PosItem item;
    private boolean isFinished = true;
    private Creature owner;

    public Task() {
    }

    public Task(Coords dest) {
        this.dest = dest;
    }

    public void cloneTo(Creature cr) {
        Task task = cr.getTask();
        task.setItem(this.item);
        task.setDest(this.dest);
        task.setOwner(cr);
    }

    public void doTask(Creature cr) {
        if (isFinished) {
            cr.getAnimationPos().setCurAnimation(CreatureAnimationType.IDLE);
            cr.getBaseAnimationPos().setBaseAnimationType(CreatureBaseAnimationType.IDLE);
            return;
        }

        move(cr);
        if (item != null) {
            if (item.creaturePrimaryInteract(cr)) {
                item = null;
                dest.x = -1;
            }
        }
        if (dest.x == -1) {
            cr.getAnimationPos().setCurAnimation(CreatureAnimationType.STOP);
            if (item == null){
                isFinished = true;
            }
        }
    }

    private void move(Creature cr) {
        if (dest.x == -1) {
            return;
        }
        Inventory inventory = cr.getIndividualInventory();
        String name = cr.getName();
        if (inventory.getActualWeight() > inventory.getMaxWeight()) {
            System.out.println(name + " overloaded");
            setFinished(true);
            return;
        }
        if (inventory.getFilledSpace() > inventory.getMaxSize()) {
            System.out.println(name + " carry too much");
            setFinished(true);
            return;
        }
        Coords pos = cr.getPos();
        double x1 = pos.x;
        double x2 = dest.x;
        double y1 = pos.y;
        double y2 = dest.y;
        int meter = Sizes.getMeter();
        if (x1 <= x2 + 10.0/ meter && x1 >= x2 - 10.0/ meter
                && y1 <= y2 + 10.0/ meter && y1 >= y2 - 10.0/ meter) {
            dest.x = -1;
            return;
        }
        double dist = Geometry.getDistance(pos, dest);
        double moveDist = cr.getSpeed();
        double x3 = x1 + (moveDist * (x2 - x1) / dist) / SECOND;
        double y3 = y1 + (moveDist * (y2 - y1) / dist) / SECOND;

        nextPos.x = x3;
        nextPos.y = y3;
        nextPos.level = pos.level;
        PosItem pi = cr.getCollision(cr.getCenterFrom(nextPos));
        if (pi != null) {
            boolean isNotCreatureCollision = !(pi instanceof Creature);
            if (isNotCreatureCollision) {
                dest.x = -1;
            }
            return;
        }
        cr.getAnimationPos().setCurAnimation(CreatureAnimationType.MOVE);
        cr.getBaseAnimationPos().setBaseAnimationType(CreatureBaseAnimationType.ACTION);
        pos.x = nextPos.x;
        pos.y = nextPos.y;
    }

    public void clear() {
        this.dest.x = -1;
        this.dest.y = -1;
        this.item = null;
    }

    public PosItem getItem() {
        return item;
    }

    public void setItem(PosItem item) {
        this.item = item;
        if (item == null) {
            return;
        }
        Coords dest;
        if (item instanceof Creature) {
            dest = owner.getReversedCenter(item.getCenter());
        } else {
            dest = owner.getReversedCenter(item.getInteractionPoint());
        }
        setDest(dest);
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
        this.dest.x = dest.x;
        this.dest.y = dest.y;
        this.dest.level = dest.level;
    }

    public void setDestX(double x) {
        this.dest.x = x;
    }

    public void setDestY(double y) {
        this.dest.y = y;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    public void setOwner(Creature owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Task{" +
                ", item=" + item +
                ", dest=" + dest +
                ", finished=" + isFinished +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return isFinished() == task.isFinished() &&
                Objects.equals(getItem(), task.getItem()) &&
                Objects.equals(getDest(), task.getDest());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getItem(), getDest(), isFinished());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(dest);

        out.writeObject(item);

        out.writeBoolean(isFinished);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        dest = (Coords) in.readObject();

        item = (PosItem) in.readObject();

        isFinished = in.readBoolean();
    }
}
