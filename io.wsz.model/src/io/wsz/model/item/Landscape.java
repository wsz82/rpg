package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.list.AbstractItemsList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static io.wsz.model.sizes.Paths.IDLE;

public class Landscape extends PosItem<Landscape, AnimationPos> {
    private static final long serialVersionUID = 1L;

    private Animation<AnimationPos, Landscape> animation;

    private AnimationPos animationPos;

    public Landscape() {}

    public Landscape(Controller controller) {
        super(ItemType.LANDSCAPE, controller);
        this.animationPos = new AnimationPos();
    }

    public Landscape(Landscape prototype) {
        super(prototype);
        this.animationPos = new AnimationPos();
    }

    @Override
    public void addItemToList(AbstractItemsList list) {
        list.getLandscapes().add(this);
    }

    @Override
    public void removeItemFromList(AbstractItemsList list) {
        list.getLandscapes().remove(this);
    }

    @Override
    protected String getAssetDirName() {
        return Paths.LANDSCAPES;
    }

    @Override
    protected Animation<AnimationPos, Landscape> getConcreteAnimation() {
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
    protected List<Landscape> getSpecificItemsList(ItemsList itemsList) {
        return itemsList.getLandscapes();
    }

    @Override
    protected Landscape getNewItemFromPrototype() {
        return new Landscape(this);
    }

    @Override
    protected <P extends PosItem<?, ?>> boolean ifItemTypeCannotCollide(P obstacle) {
        return obstacle.ifItemTypeCannotCollideWithLandscape();
    }

    @Override
    protected boolean ifItemTypeCannotCollideWithLandscape() {
        return true;
    }

    @Override
    protected boolean ifItemTypeCannotCollideWithDoor() {
        return true;
    }

    @Override
    protected boolean ifItemTypeCannotCollideWithCover() {
        return true;
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
