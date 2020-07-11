package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Equipment<E extends Equipment> extends PosItem<E> implements Equippable {
    private static final long serialVersionUID = 1L;

    public static List<Equipment> cloneEquipmentList(List<Equipment> equipment) {
        List<Equipment> clone = new ArrayList<>(equipment.size());
        for (Equipment e : equipment) {
            clone.add(e.cloneEquipment());
        }
        return clone;
    }

    protected Double weight;
    protected Integer size;

    public Equipment() {}

    public Equipment(E prototype, String name, ItemType type, String path,
                     Boolean visible, Integer level,
                     List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, level,
                coverLine, collisionPolygons);
    }
    public Double getIndividualWeight() {
        return weight;
    }

    public Double getWeight() {
        if (weight == null) {
            if (prototype == null) {
                return 0.0;
            }
            return prototype.weight;
        }
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getIndividualSize() {
        return size;
    }

    public Integer getSize() {
        if (size == null) {
            if (prototype == null) {
                return 1;
            }
            return prototype.size;
        }
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public abstract E cloneEquipment();

    @Override
    public void onTake(Creature cr, double x, double y) {
        pos.x = x;
        pos.y = y;
        level = 0;
        setVisible(false);
        cr.pos.getLocation().getItemsToRemove().add(this);
        System.out.println(this.getName() + " taken");
    }

    @Override
    public boolean onDrop(Creature cr, double x, double y) {
        double tempX = pos.x;
        double tempY = pos.y;
        Integer tempLevel = this.getLevel();
        pos.x = x;
        pos.y = y;
        setLevel(cr.getLevel());
        Location l = cr.getPos().getLocation();
        PosItem obstacle = Controller.get().getBoard().getObstacle(pos, this, l);
        boolean outOfLocation = x < 0 || y < 0
                || x > l.getWidth() || y > l.getHeight();
        if (obstacle != null || outOfLocation) {
            pos.x = tempX;
            pos.y = tempY;
            setLevel(tempLevel);
            System.out.println(this.getName() + " cannot be dropped here");
            return false;
        } else {
            l.getItemsToAdd().add(this);
            System.out.println(this.getName() + " dropped");
            return true;
        }
    }

    @Override
    public void onEquip() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        if (!super.equals(o)) return false;
        Equipment<?> equipment = (Equipment<?>) o;
        return Objects.equals(getWeight(), equipment.getWeight()) &&
                Objects.equals(getSize(), equipment.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWeight(), getSize());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(serialVersionUID);

        out.writeObject(weight);

        out.writeObject(size);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        weight = (Double) in.readObject();

        size = (Integer) in.readObject();
    }
}
