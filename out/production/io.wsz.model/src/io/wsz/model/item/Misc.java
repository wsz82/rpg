package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimation;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.sizes.Paths;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.sizes.Paths.IDLE;

public class Misc extends EquipmentMayCountable<Misc, EquipmentAnimationPos> {
    private static final long serialVersionUID = 1L;

    private EquipmentAnimation<Misc> animation;

    private EquipmentAnimationPos animationPos;

    public Misc() {}

    public Misc(Controller controller) {
        super(ItemType.MISC, controller);
        this.animationPos = new EquipmentAnimationPos();
    }

    public Misc(Misc prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new EquipmentAnimationPos();
    }

    public Misc(Misc other) {
        super(other);
        this.animationPos = new EquipmentAnimationPos(other.getAnimationPos());
        this.equipmentType = other.equipmentType;
    }

    @Override
    public Misc cloneEquipment() {
        return new Misc(this);
    }

    @Override
    protected EquipmentAnimation<Misc> getConcreteAnimation() {
        if (animation == null) {
            return new EquipmentAnimation<>(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public EquipmentAnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    protected String getAssetDirName() {
        return Paths.MISC;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(animationPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        animationPos = (EquipmentAnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new EquipmentAnimation<>(getDir(), IDLE);
        }
    }
}
