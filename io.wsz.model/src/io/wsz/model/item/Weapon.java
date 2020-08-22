package io.wsz.model.item;

import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.weapon.WeaponAnimation;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Weapon extends Equipment<Weapon, EquipmentAnimationPos> {
    private static final long serialVersionUID = 1L;

    private WeaponAnimation animation;

    private final EquipmentAnimationPos animationPos;
    private Double damage;
    private Double range;
    private Double speed;
    private WeaponType weaponType;

    public Weapon() {
        this.animationPos = new EquipmentAnimationPos();
    }

    public Weapon(ItemType type) {
        super(type);
        this.animation = new WeaponAnimation(getDir());
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
        this.weaponType = other.weaponType;
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

    public WeaponType getIndividualWeaponType() {
        return weaponType;
    }

    public WeaponType getWeaponType() {
        if (weaponType == null) {
            if (prototype == null) {
                return WeaponType.DEFAULT;
            }
            return prototype.weaponType;
        } else {
            return weaponType;
        }
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = weaponType;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            boolean fits = cr.getIndividualInventory().fitsInventory(this);
            if (fits) {
                cr.getIndividualInventory().add(this);
                onTake(cr, 0, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public WeaponAnimation getAnimation() {
        if (prototype == null) {
            return animation;
        } else {
            return prototype.getAnimation();
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

        out.writeObject(damage);

        out.writeObject(range);

        out.writeObject(speed);

        out.writeObject(weaponType);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        if (prototype == null) {
            animation = new WeaponAnimation(getDir());
        }

        damage = (Double) in.readObject();

        range = (Double) in.readObject();

        speed = (Double) in.readObject();

        weaponType = (WeaponType) in.readObject();
    }
}
