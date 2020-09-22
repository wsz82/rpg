package io.wsz.model.animation.creature;

import io.wsz.model.animation.AnimationPos;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CreatureBaseAnimationPos extends AnimationPos {
    private static final long serialVersionUID = 1L;

    private CreatureBaseAnimationType baseAnimationType;

    public CreatureBaseAnimationPos() {
        this.baseAnimationType = CreatureBaseAnimationType.IDLE;
    }

    public CreatureBaseAnimationPos(CreatureBaseAnimationPos other) {
        super(other);
        this.baseAnimationType = other.baseAnimationType;
    }

    public CreatureBaseAnimationType getBaseAnimationType() {
        return baseAnimationType;
    }

    public void setBaseAnimationType(CreatureBaseAnimationType baseAnimationType) {
        this.baseAnimationType = baseAnimationType;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(baseAnimationType);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        baseAnimationType = (CreatureBaseAnimationType) in.readObject();
    }
}
