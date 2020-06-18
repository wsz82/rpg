package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Equipment<E extends Equipment> extends PosItem<E> implements Equippable {
    protected Double weight;
    protected Integer size;

    public Equipment() {}

    public Equipment(E prototype, String name, ItemType type, String path,
                     Boolean visible, Coords pos, Integer level) {
        super(prototype, name, type, path, visible, pos, level, null, null);
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
        pos = cr.pos;
        level = cr.level;
        Controller.get().getCurrentLocation().getLocation().getItemsToRemove().add(this);
    }

    @Override
    public void onDrop(Creature cr) {
        visible.set(true);
        this.pos = cr.posToCenter();
        this.level = cr.getLevel();
        Controller.get().getCurrentLocation().getLocation().getItemsToAdd().add(this);
    }

    @Override
    public void update() {

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
