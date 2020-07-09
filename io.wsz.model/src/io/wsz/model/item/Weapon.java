package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Weapon extends Equipment<Weapon> {
    private static final long serialVersionUID = 1L;

    protected Double damage;
    protected Double range;
    protected Double speed;

    public Weapon() {}

    public Weapon(Weapon prototype, String name, ItemType type, String path,
                  Boolean visible, Integer level,
                  List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, level,
                coverLine, collisionPolygons);
    }

    @Override
    public Weapon cloneEquipment() {
        Weapon clone = new Weapon(prototype, name.get(), type.get(), relativePath.get(), visible.get(), level,
                Coords.cloneCoordsList(prototype.coverLine), Coords.cloneCoordsPolygons(prototype.getCollisionPolygons()));
        clone.getPos().x = this.pos.x;
        clone.getPos().y = this.pos.y;
        clone.setWeight(weight);
        clone.setSize(size);
        clone.setDamage(damage);
        clone.setRange(range);
        clone.setSpeed(speed);
        return clone;
    }

    public Double getIndividualDamage() {
        return damage;
    }

    public Double getDamage() {
        if (damage == null) {
            if (prototype == null) {
                return 0.0;
            }
            return prototype.damage;
        } else {
            return damage;
        }
    }

    public void setDamage(Double damage) {
        this.damage = damage;
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

    @Override
    public void onEquip() {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(serialVersionUID);

        out.writeObject(damage);

        out.writeObject(range);

        out.writeObject(speed);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        damage = (Double) in.readObject();

        range = (Double) in.readObject();

        speed = (Double) in.readObject();
    }
}
