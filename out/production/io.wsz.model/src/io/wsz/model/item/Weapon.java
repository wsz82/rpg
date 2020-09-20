package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.countable.EquipmentMayCountableAnimation;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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

    public Weapon(Weapon prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new EquipmentAnimationPos();
    }

    public Weapon(Weapon other) {
        super(other);
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
    public Weapon cloneEquipment() {
        return new Weapon(this);
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
