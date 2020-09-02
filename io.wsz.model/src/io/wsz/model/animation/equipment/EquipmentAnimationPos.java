package io.wsz.model.animation.equipment;

import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EquipmentAnimationPos extends AnimationPos {
    private static final long serialVersionUID = 1L;

    protected EquipmentAnimationType curAnimation;

    public EquipmentAnimationPos() {
        this.curAnimation = EquipmentAnimationType.DROP;
    }

    public EquipmentAnimationPos(EquipmentAnimationPos other) {
        super(other);
        this.curAnimation = other.curAnimation;
    }

    public EquipmentAnimationType getCurAnimation() {
        return curAnimation;
    }

    public void setCurAnimation(EquipmentAnimationType curAnimation) {
        if (this.curAnimation == curAnimation) return;
        frameNumber = 0;
        nextFrameUpdate = 0;
        this.curAnimation = curAnimation;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(curAnimation);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        curAnimation = (EquipmentAnimationType) in.readObject();
    }
}
