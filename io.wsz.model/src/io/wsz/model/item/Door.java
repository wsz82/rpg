package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.door.DoorAnimation;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public abstract class Door<I extends Door<?>> extends PosItem<I, AnimationPos> implements Openable {
    private static final long serialVersionUID = 1L;

    private DoorAnimation animation;

    private final AnimationPos animationPos;
    private OpenableItem openableItem;
    protected boolean isOpen;

    public Door() {
        this.animationPos = new AnimationPos();
    }

    public Door(ItemType type, Controller controller) {
        super(type, controller);
        this.animationPos = new AnimationPos();
        openableItem = new OpenableItem();
    }

    public Door(I prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new AnimationPos();
    }

    @Override
    public ResolutionImage getOpenImage() {
        File programDir = getController().getProgramDir();
        DoorAnimation animation = getAnimation();
        if (animation == null) return null;
        return animation.getOpenableAnimation().getBasicMainOpen(programDir);
    }

    public ResolutionImage getEditorImage() {
        if (isOpen) {
            if (isThisPrototype()) {
                return getOpenImage();
            } else {
                return prototype.getOpenImage();
            }
        } else {
            if (isThisPrototype()) {
                return getInitialImage();
            } else {
                return prototype.getInitialImage();
            }
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public OpenableItem getIndividualOpenableItem() {
        return openableItem;
    }

    public OpenableItem getOpenableItem() {
        if (isThisPrototype()) {
            return openableItem;
        } else {
            return prototype.getOpenableItem();
        }
    }

    public void setOpenableItem(OpenableItem openableItem) {
        this.openableItem = openableItem;
    }

    @Override
    public ResolutionImage getImage() {
        if (image == null) {
            File programDir = getController().getProgramDir();
            DoorAnimation animation = getAnimation();
            if (isOpen()) {
                return animation.getOpenableAnimation().getBasicMainOpen(programDir);
            } else {
                return animation.getBasicMain(programDir);
            }
        } else {
            return image;
        }
    }

    @Override
    protected DoorAnimation getConcreteAnimation() {
        if (animation == null) {
            return new DoorAnimation(getDir());
        } else {
            return animation;
        }
    }

    @Override
    public AnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public List<Coords> getActualCoverLine() {
        if (isOpen) {
            return getOpenableItem().getOpenCoverLine();
        } else {
            return super.getActualCoverLine();
        }
    }

    @Override
    public List<List<Coords>> getActualCollisionPolygons() {
        if (isOpen) {
            return getOpenableItem().getOpenCollisionPolygons();
        } else {
            return super.getActualCollisionPolygons();
        }
    }

    @Override
    public void open() {
        isOpen = true;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = false;
            System.out.println(getAssetId() + " cannot be open: collides with " + collision.getAssetId());
        } else {
            System.out.println(getAssetId() + " open");
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = true;
            System.out.println(getAssetId() + " cannot be closed: collides with " + collision.getAssetId());
        } else {
            System.out.println(getAssetId() + " closed");
        }
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            if (isOpen) {
                close();
            } else {
                open();
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeBoolean(isOpen);

        out.writeObject(openableItem);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        if (isThisPrototype()) {
            animation = new DoorAnimation(getDir());
        }

        isOpen = in.readBoolean();

        openableItem = (OpenableItem) in.readObject();
    }
}
