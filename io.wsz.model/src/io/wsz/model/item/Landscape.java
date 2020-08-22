package io.wsz.model.item;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Landscape extends PosItem<Landscape, AnimationPos> {
    private static final long serialVersionUID = 1L;

    private Animation<Landscape> animation;

    private final AnimationPos animationPos;

    public Landscape() {
        this.animationPos = new AnimationPos();
    }

    public Landscape(ItemType type) {
        super(type);
        this.animation = new Animation<>(getDir());
        this.animationPos = new AnimationPos();
    }

    public Landscape(Landscape prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new AnimationPos();
    }

    @Override
    public Animation<Landscape> getAnimation() {
        if (prototype == null) {
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

        if (prototype == null) {
            animation = new Animation<>(getDir());
        }
    }
}
