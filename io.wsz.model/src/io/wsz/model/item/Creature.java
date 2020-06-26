package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.List;

import static io.wsz.model.item.CreatureControl.*;
import static io.wsz.model.item.ItemType.TELEPORT;
import static io.wsz.model.sizes.Sizes.METER;
import static io.wsz.model.sizes.Sizes.SECOND;

public class Creature extends PosItem<Creature> implements Containable {
    private static final long serialVersionUID = 1L;

    private ArrayDeque<Task> tasks;
    private Inventory inventory;
    private CreatureSize size;
    private CreatureControl control;
    private Double speed;
    private Double range;
    private Integer strength;

    public Creature() {}

    public Creature(Creature prototype, String name, ItemType type, String path,
                    Boolean visible, Coords pos, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
    }

    private PosItem getCollision(Coords nextPos) {
        Coords[] poss = getCorners(posToCenter(nextPos));
        PosItem collidedObstacle = Board.get().lookForObstacle(poss);
        if (collidedObstacle == null) {
            Creature collidedCreature = Board.get().getCornersCreature(poss, this);
            return collidedCreature;
        }
        return collidedObstacle;
    }

    private void checkSurrounding() {
        ItemType[] types = new ItemType[] {TELEPORT};
        PosItem pi = getCornersContent(types);
        if (pi != null) {
            ItemType type = pi.getType();
            switch (type) {
                case TELEPORT -> ((Teleport) pi).enter(this);
            }
        }
    }

    private PosItem getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords posToCenter() {
        return posToCenter(pos);
    }

    public Coords posToCenter(Coords pos) {
        double width = getImage().getWidth() / METER;
        double height = getImage().getHeight() / METER;
        double x = pos.x + width/2;
        double y = pos.y + height;
        return new Coords(x, y);
    }

    public Coords centerToPos(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth() / METER;
        double height = getImage().getHeight() / METER;
        double x = difPos.x - width/2;
        double y = difPos.y - height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        Coords centerBottomPos = posToCenter();
        return getCorners(centerBottomPos);
    }

    public Coords[] getCorners(Coords pos) {
        double halfWidth = getSize().getWidth()/2;
        double halfHeight = getSize().getHeight()/2;
        double centerX = pos.x;
        double centerY = pos.y;

        Coords N = new Coords(centerX, centerY - halfHeight);
        Coords W = new Coords(centerX - halfWidth, centerY);
        Coords S = new Coords(centerX, centerY + halfHeight);
        Coords E = new Coords(centerX + halfWidth, centerY);
        Coords NE = new Coords(centerX + 3/5.0*halfWidth, centerY - 2/3.0*halfHeight);
        Coords NW = new Coords(centerX - 3/5.0*halfWidth, centerY - 2/3.0*halfHeight);
        Coords SE = new Coords(centerX + 3/5.0*halfWidth, centerY + 2/3.0*halfHeight);
        Coords SW = new Coords(centerX - 3/5.0*halfWidth, centerY + 2/3.0*halfHeight);
        return new Coords[] {N, NW, W, SW, S, SE, E, NE};
    }

    public void loseControl() {
        setControl(CONTROLLABLE);
    }

    public void onInteractWith(Coords pos) {
        Coords[] poss = new Coords[] {pos};
        ItemType[] types = ItemType.values();
        PosItem pi = Controller.get().getBoard().lookForContent(poss, types, true);
        if (pi == null) {
            return;
        }
        ItemType type = pi.getType();

        switch (type) {
            case CREATURE ->
                    resolveInteractionWithCreature((Creature) pi);
            case WEAPON ->
                    takeItem((Equipment) pi);
            case CONTAINER ->
                    openContainer((Container) pi);
            default ->
                    goTo(pos);
        }
    }

    private void resolveInteractionWithCreature(Creature cr) {
        CreatureControl control = cr.getControl();
        if (control.equals(CONTROLLABLE)) {
            cr.setControl(CONTROL);
            this.setControl(CONTROLLABLE);
        } else if (control.equals(NEUTRAL)) {
            startConversation(cr);
        }
    }

    private void goTo(Coords pos) {
        Task task = new Task(centerToPos(pos));
        tasks.clear();
        tasks.push(task);
    }

    private void takeItem(Equipment e) {
        Task task = new Task(e);
        tasks.clear();
        tasks.push(task);
    }

    private void openContainer(Container c) {
        Task task = new Task(c);
        tasks.clear();
        tasks.push(task);
    }

    private void startConversation(Creature cr) {
        Task task = new Task(cr);
        tasks.clear();
        tasks.push(task);
    }

    private boolean creatureWithinRange(Creature cr) {
        Coords ePos = cr.posToCenter();
        Coords[] poss = getCorners();
        for (Coords corner : poss) {
            double dist = getDistance(corner.x, ePos.x, corner.y, ePos.y);
            if (dist <= getRange()) {
                return true;
            }
        }
        return false;
    }

    private boolean withinRange(PosItem e) {
        Coords ePos = e.getCenter();
        Coords[] poss = getCorners();
        for (Coords corner : poss) {
            double dist = getDistance(corner.x, ePos.x, corner.y, ePos.y);
            if (dist <= getRange()) {
                return true;
            }
        }
        return false;
    }

    public List<Equipment> getEquipmentWithinRange() {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().getEquipmentWithinRange(poss, this);
    }

    private double getDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void checkTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        Task priority = tasks.getFirst();
        priority.doTask();
        if (priority.finished) {
            tasks.remove(priority);
        }
    }

    public ArrayDeque<Task> getIndividualTasks() {
        return tasks;
    }

    public ArrayDeque<Task> getTasks() {
        if (tasks == null) {
            if (prototype == null) {
                return new ArrayDeque<>(0);
            }
            return prototype.tasks;
        } else {
            return tasks;
        }
    }

    public void setTasks(ArrayDeque<Task> tasks) {
        this.tasks = tasks;
    }

    public Inventory getIndividualInventory() {
        return inventory;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            if (prototype == null) {
                return new Inventory(this);
            }
            return prototype.inventory;
        } else {
            return inventory;
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Double getIndividualSpeed() {
        return speed;
    }

    public Double getSpeed() {
        if (speed == null) {
            if (prototype == null) {
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
            if (prototype == null) {
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

    public CreatureSize getIndividualSize() {
        return size;
    }

    public CreatureSize getSize() {
        if (size == null) {
            if (prototype == null) {
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
            if (prototype == null) {
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
            if (prototype == null) {
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

    @Override
    public void changeLocation(Location from, Location target, Layer targetLayer, double targetX, double targetY) {
        super.changeLocation(from, target, targetLayer, targetX, targetY);
        pos = centerToPos(new Coords(targetX, targetY));
        tasks.clear();
    }

    @Override
    public void update() {
        checkSurrounding();
        checkTasks();
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(tasks);

        out.writeObject(inventory);

        out.writeObject(size);

        out.writeObject(control);

        out.writeObject(speed);

        out.writeObject(range);

        out.writeObject(strength);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        tasks = (ArrayDeque<Task>) in.readObject();

        inventory = (Inventory) in.readObject();

        size = (CreatureSize) in.readObject();

        control = (CreatureControl) in.readObject();

        speed = (Double) in.readObject();

        range = (Double) in.readObject();

        strength = (Integer) in.readObject();
    }

    public class Task implements Serializable {
        private PosItem item;
        private Coords dest;
        private boolean finished;

        public Task() {}

        public Task(Coords dest) {
            this.dest = dest;
        }

        public Task(PosItem item) {
            this.item = item;
            if (item instanceof Creature) {
                this.dest = centerToPos(((Creature) item).posToCenter());
            } else {
                this.dest = centerToPos(item.getPos());
            }
        }

        public Task clone() {
            Task task = new Task();
            task.item = this.item;
            task.dest = this.dest;
            return task;
        }

        private void doTask() {
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
            if (creatureWithinRange(cr)) {
                Controller.get().setAsking(Creature.this);
                Controller.get().setAnswering(cr);
                item = null;
                dest = null;
            }
        }

        private void interactWithEquipment(Equipment equipment) {
            if (withinRange(equipment)) {
                if (equipment instanceof Container) {
                    ((Container) equipment).open(Creature.this);
                } else {
                    boolean taken = inventory.add(equipment);
                    if (taken) {
                        equipment.onTake(Creature.this);
                    }
                }
                item = null;
                dest = null;
            }
        }

        private void move() {
            if (inventory.getActualWeight() > inventory.getMaxWeight()) {
                return;
            }
            double x1 = pos.x;
            double x2 = dest.x;
            double y1 = pos.y;
            double y2 = dest.y;
            if (x1 <= x2 + 10.0/METER && x1 >= x2 - 10.0/METER
                    && y1 <= y2 + 10.0/METER && y1 >= y2 - 10.0/METER) {
                dest = null;
                return;
            }
            double dist = getDistance(x1, x2, y1, y2);
            double moveDist = getSpeed();
            if (dist < getSpeed()) {
                moveDist = dist;
            }
            double x3 = x1 + (moveDist/dist * (x2 - x1)) / SECOND;
            double y3 = y1 + (moveDist/dist * (y2 - y1)) / SECOND;
            Coords nextPos = new Coords(x3, y3);
            PosItem pi = getCollision(nextPos);
            if (pi != null) {
                dest = null;
                return;
            }
            pos = nextPos;
        }
    }
}
