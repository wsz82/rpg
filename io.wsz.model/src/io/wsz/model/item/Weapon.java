package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.countable.EquipmentMayCountableAnimation;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

import static io.wsz.model.sizes.Paths.IDLE;

public class Weapon extends EquipmentMayCountable<Weapon, EquipmentAnimationPos> {
    private static final long serialVersionUID = 1L;

    private EquipmentMayCountableAnimation<Weapon> animation;

    private EquipmentAnimationPos animationPos;
    private Double damage;
    private Double range;
    private Double speed;

    public Weapon() {}

    public Weapon(Controller controller) {
        super(ItemType.WEAPON, controller);
        this.animationPos = new EquipmentAnimationPos();
    }

    public Weapon(Weapon prototype) {
        super(prototype);
        this.animationPos = new EquipmentAnimationPos();
    }

    public Weapon(Weapon other, boolean keepId) {
        super(other, keepId);
        this.animationPos = new EquipmentAnimationPos(other.getAnimationPos());
        this.damage = other.damage;
        this.range = other.range;
        this.speed = other.speed;
        this.equipmentType = other.equipmentType;
    }

    @Override
    protected String getAssetDirName() {
        return Paths.WEAPONS;
    }

    @Override
    public Weapon cloneEquipment(boolean keepId) {
        return new Weapon(this, keepId);
    }

    public Double getIndividualDamage() {
        return damage;
    }

    public Double getDamage() {
        if (damage == null) {
            if (isThisPrototype()) {
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

    @Override
    protected EquipmentMayCountableAnimation<Weapon> getConcreteAnimation() {
        if (animation == null) {
            return new EquipmentMayCountableAnimation<>(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public EquipmentAnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public boolean isUnitIdentical(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;
        if (!super.isUnitIdentical(o)) return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(getDamage(), weapon.getDamage()) &&
                Objects.equals(getRange(), weapon.getRange()) &&
                Objects.equals(getSpeed(), weapon.getSpeed());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weapon)) return false;
        if (!super.equals(o)) return false;
        Weapon weapon = (Weapon) o;
        return Objects.equals(getDamage(), weapon.getDamage()) &&
                Objects.equals(getRange(), weapon.getRange()) &&
                Objects.equals(getSpeed(), weapon.getSpeed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getDamage(), getRange(), getSpeed());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(animationPos);

        out.writeObject(damage);

        out.writeObject(range);

        out.writeObject(speed);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        animationPos = (EquipmentAnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new EquipmentMayCountableAnimation<>(getDir(), IDLE);
        }

        damage = (Double) in.readObject();

        range = (Double) in.readObject();

        speed = (Double) in.readObject();
    }
}
