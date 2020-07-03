package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Serializable;
import java.util.Objects;

import static io.wsz.model.sizes.Sizes.SECOND;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Creature creature;
    private final Coords nextPos = new Coords(-1, -1, null);

    private final Coords dest = new Coords(-1, -1, null);
    private PosItem item;
    private boolean finished;

    public Task(Creature creature) {
        this.creature = creature;
    }

    public void clone(Creature cr) {
        Task task = cr.getTask();
        task.setItem(this.item);
        task.setDest(this.dest);
    }

    public void doTask() {
        if (dest.x != -1) {
            move();
            if (item != null) {
                if (item instanceof Equipment) {
                    interactWithEquipment((Equipment) item);
                } else
                if (item instanceof Creature) {
                    interactWithCreature((Creature) item);
                }
            }
            return;
        }
        finished = true;
    }

    private void interactWithCreature(Creature cr) {
        if (creature.creatureWithinRange(cr)) {
            Controller.get().setAsking(creature);
            Controller.get().setAnswering(cr);
            item = null;
            dest.x = -1;
        }
    }

    private void interactWithEquipment(Equipment equipment) {
        if (creature.withinRange(equipment)) {
            if (equipment instanceof Container) {
                ((Container) equipment).open(creature);
            } else {
                boolean taken = creature.getIndividualInventory().add(equipment);
                if (taken) {
                    equipment.onTake(creature);
                }
            }
            item = null;
            dest.x = -1;
        }
    }

    private void move() {
        if (creature.getIndividualInventory().getActualWeight() > creature.getIndividualInventory().getMaxWeight()) {
            return;
        }
        double x1 = creature.pos.x;
        double x2 = dest.x;
        double y1 = creature.pos.y;
        double y2 = dest.y;
        if (x1 <= x2 + 10.0/ Sizes.getMeter() && x1 >= x2 - 10.0/ Sizes.getMeter()
                && y1 <= y2 + 10.0/ Sizes.getMeter() && y1 >= y2 - 10.0/ Sizes.getMeter()) {
            dest.x = -1;
            return;
        }
        double dist = creature.getDistance(x1, x2, y1, y2);
        double moveDist = creature.getSpeed();
        if (dist < creature.getSpeed()) {
            moveDist = dist;
        }
        double x3 = x1 + (moveDist/dist * (x2 - x1)) / SECOND;
        double y3 = y1 + (moveDist/dist * (y2 - y1)) / SECOND;
        nextPos.x = x3;
        nextPos.y = y3;
        PosItem pi = creature.getCollision(creature.getCenterBottomPos(nextPos));
        if (pi != null) {
            dest.x = -1;
            return;
        }
        creature.pos.x = nextPos.x;
        creature.pos.y = nextPos.y;
    }

    public void clear() {
        this.dest.x = -1;
        this.dest.y = -1;
        this.item = null;
    }

    public Creature getCreature() {
        return creature;
    }

    public PosItem getItem() {
        return item;
    }

    public void setItem(PosItem item) {
        this.item = item;
        if (item == null) {
            return;
        }
        if (item instanceof Creature) {
            Coords dest = creature.reverseCenterBottomPos(((Creature) item).getCenterBottomPos());
            this.dest.x = dest.x;
            this.dest.y = dest.y;
        } else {
            Coords dest = creature.reverseCenterBottomPos(item.getPos());
            this.dest.x = dest.x;
            this.dest.y = dest.y;
        }
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
        this.dest.x = dest.x;
        this.dest.y = dest.y;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "Task{" +
                "creature=" + creature +
                ", item=" + item +
                ", dest=" + dest +
                ", finished=" + finished +
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
}
