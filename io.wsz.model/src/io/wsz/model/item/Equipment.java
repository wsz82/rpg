package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public abstract class Equipment<E extends Equipment> extends PosItem<E> implements Equippable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public void onTake(Creature cr) {
        visible.set(false);
        Coords crPos = cr.pos;
        this.pos.x = crPos.x;
        this.pos.y = crPos.y;
        level = cr.level;
        crPos.getLocation().getItemsToRemove().add(this);
    }

    @Override
    public void onDrop(Creature cr) {
        visible.set(true);
        Coords centerBottomPos = cr.getCenterBottomPos();
        this.pos.x = centerBottomPos.x;
        this.pos.y = centerBottomPos.y;
        this.level = cr.getLevel();
        cr.getPos().getLocation().getItemsToAdd().add(this);
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

        out.writeObject(weight);

        out.writeObject(size);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        weight = (Double) in.readObject();

        size = (Integer) in.readObject();
    }
}
