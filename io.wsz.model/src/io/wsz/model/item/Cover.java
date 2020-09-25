package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.sizes.Paths.IDLE;

public class Cover extends PosItem<Cover, AnimationPos> implements Externalizable {
    private static final long serialVersionUID = 1L;

    private Animation<Cover> animation;

    private AnimationPos animationPos;

    public Cover() {}

    public Cover(Controller controller) {
        super(ItemType.COVER, controller);
        this.animationPos = new AnimationPos();
    }

    public Cover(Cover prototype) {
        super(prototype);
        this.animationPos = new AnimationPos();
    }

    @Override
    protected String getAssetDirName() {
        return Paths.COVERS;
    }

    @Override
    protected Animation<Cover> getConcreteAnimation() {
        if (animation == null) {
            return new Animation<>(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public AnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    protected Cover getNewItemFromPrototype() {
        return new Cover(this);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(animationPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        animationPos = (AnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new Animation<>(getDir(), IDLE);
        }
    }
}
