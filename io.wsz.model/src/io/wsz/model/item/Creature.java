package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.wsz.model.item.CreatureControl.*;
import static io.wsz.model.item.ItemType.TELEPORT;

public class Creature extends PosItem<Creature> implements Containable {
    private static final long serialVersionUID = 1L;

    private Image portrait;
    private String portraitPath;
    private LinkedList<Task> tasks;
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

    public PosItem getCollision(Coords nextPos) {
        Coords[] poss = getCorners(nextPos);
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

        PosItem collided = getCollision(getCenterBottomPos());
        if (collided != null) {
            Coords freePos = Board.get().getFreePosAround(this);
            pos = reverseCenterBottomPos(freePos);
        }
    }

    private PosItem getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords getCenterBottomPos() {
        return getCenterBottomPos(pos);
    }

    public Coords getCenterBottomPos(Coords pos) {
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        double x = pos.x + width/2;
        double y = pos.y + height;
        return new Coords(x, y);
    }

    public Coords reverseCenterBottomPos(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        double x = difPos.x - width/2;
        double y = difPos.y - height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        Coords centerBottomPos = getCenterBottomPos();
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
        Task task = new Task(this, reverseCenterBottomPos(pos));
        tasks.clear();
        tasks.push(task);
    }

    private void takeItem(Equipment e) {
        Task task = new Task(this, e);
        tasks.clear();
        tasks.push(task);
    }

    private void openContainer(Container c) {
        Task task = new Task(this, c);
        tasks.clear();
        tasks.push(task);
    }

    private void startConversation(Creature cr) {
        Task task = new Task(this, cr);
        tasks.clear();
        tasks.push(task);
    }

    public boolean creatureWithinRange(Creature cr) {
        Coords ePos = cr.getCenterBottomPos();
        Coords[] poss = getCorners();
        for (Coords corner : poss) {
            double dist = getDistance(corner.x, ePos.x, corner.y, ePos.y);
            if (dist <= getRange()) {
                return true;
            }
        }
        return false;
    }

    public boolean withinRange(PosItem e) {
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

    public double getDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void checkTasks() {
        if (tasks.isEmpty()) {
            return;
        }
        Task priority = tasks.getFirst();
        priority.doTask();
        if (priority.isFinished()) {
            tasks.remove(priority);
        }
    }

    public LinkedList<Task> getIndividualTasks() {
        return tasks;
    }

    public LinkedList<Task> getTasks() {
        if (tasks == null) {
            if (prototype == null) {
                return new LinkedList<>();
            }
            return prototype.tasks;
        } else {
            return tasks;
        }
    }

    public void setTasks(LinkedList<Task> tasks) {
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

    public Image getPortrait() {
        if (this.portrait == null) {
            setPortrait(loadImageFromPath(getPortraitPath()));
        }
        return portrait;
    }

    private void setPortrait(Image portrait) {
        this.portrait = portrait;
    }

    public String getIndividualPortraitPath() {
        return portraitPath;
    }

    public String getPortraitPath() {
        if (portraitPath == null || portraitPath.isEmpty()) {
            if (prototype == null) {
                return "";
            }
            return prototype.portraitPath;
        } else {
            return portraitPath;
        }
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    @Override
    public void changeLocation(Location from, Location target, Layer targetLayer, double targetX, double targetY) {
        super.changeLocation(from, target, targetLayer, targetX, targetY);
        Coords rawPos = new Coords(targetX, targetY);
        pos = reverseCenterBottomPos(rawPos);
        tasks.clear();

        IdentityHashMap<Creature, Location> heroes = Controller.get().getHeroes();
        if (heroes.containsKey(this)) {
            heroes.put(this, target);
        }
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Creature creature = (Creature) o;
        return Objects.equals(getTasks(), creature.getTasks()) &&
                Objects.equals(getInventory(), creature.getInventory()) &&
                getSize() == creature.getSize() &&
                getControl() == creature.getControl() &&
                Objects.equals(getSpeed(), creature.getSpeed()) &&
                Objects.equals(getRange(), creature.getRange()) &&
                Objects.equals(getStrength(), creature.getStrength());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTasks(), getInventory(), getSize(), getControl(), getSpeed(), getRange(), getStrength());
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

        out.writeObject(portraitPath);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        tasks = (LinkedList<Task>) in.readObject();

        inventory = (Inventory) in.readObject();

        size = (CreatureSize) in.readObject();

        control = (CreatureControl) in.readObject();

        speed = (Double) in.readObject();

        range = (Double) in.readObject();

        strength = (Integer) in.readObject();

        portraitPath = (String) in.readObject();
    }
}
