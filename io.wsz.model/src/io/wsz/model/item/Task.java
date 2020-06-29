package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Serializable;
import java.util.Objects;

import static io.wsz.model.sizes.Sizes.SECOND;

public class Task implements Serializable {
    private final Creature creature;
    private PosItem item;
    private Coords dest;
    private boolean finished;

    public Task(Creature creature) {
        this.creature = creature;
    }

    public Task(Creature creature, Coords dest) {
        this.creature = creature;
        this.dest = dest;
    }

    public Task(Creature creature, PosItem item) {
        this.creature = creature;
        this.item = item;
        if (item instanceof Creature) {
            this.dest = creature.reverseCenterBottomPos(((Creature) item).getCenterBottomPos());
        } else {
            this.dest = creature.reverseCenterBottomPos(item.getPos());
        }
    }

    public Task clone(Creature cr) {
        Task task = new Task(cr);
        task.item = this.item;
        task.dest = this.dest;
        task.finished = this.finished;
        return task;
    }

    public void doTask() {
        if (dest != null) {
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
            dest = null;
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
            dest = null;
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
            dest = null;
            return;
        }
        double dist = creature.getDistance(x1, x2, y1, y2);
        double moveDist = creature.getSpeed();
        if (dist < creature.getSpeed()) {
            moveDist = dist;
        }
        double x3 = x1 + (moveDist/dist * (x2 - x1)) / SECOND;
        double y3 = y1 + (moveDist/dist * (y2 - y1)) / SECOND;
        Coords nextPos = new Coords(x3, y3);
        PosItem pi = creature.getCollision(creature.getCenterBottomPos(nextPos));
        if (pi != null) {
            dest = null;
            return;
        }
        creature.pos = nextPos;
    }

    public Creature getCreature() {
        return creature;
    }

    public PosItem getItem() {
        return item;
    }

    public void setItem(PosItem item) {
        this.item = item;
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
        this.dest = dest;
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
