package io.wsz.model.item;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Cover extends PosItem<Cover, AnimationPos> implements Externalizable {
    private static final long serialVersionUID = 1L;

    private Animation<Cover> animation;

    private final AnimationPos animationPos;

    public Cover() {
        this.animationPos = new AnimationPos();
    }

    public Cover(ItemType type) {
        super(type);
        this.animation = new Animation<>(getDir());
        this.animationPos = new AnimationPos();
    }

    public Cover(Cover prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new AnimationPos();
    }

    @Override
    public Animation<Cover> getAnimation() {
        if (isThisPrototype()) {
            return animation;
        } else {
            return prototype.getAnimation();
        }
    }

    @Override
    public AnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        if (isThisPrototype()) {
            animation = new Animation<>(getDir());
        }
    }
}
