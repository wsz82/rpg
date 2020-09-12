package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.door.DoorAnimation;
import io.wsz.model.animation.openable.OpenableAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public abstract class Door<I extends Door<?>> extends PosItem<I, OpenableAnimationPos> implements Openable {
    private static final long serialVersionUID = 1L;

    protected OpenableAnimationPos animationPos;
    protected boolean isOpen;

    private DoorAnimation animation;

    private OpenableItem openableItem;

    public Door() {}

    public Door(ItemType type, Controller controller) {
        super(type, controller);
        this.animationPos = new OpenableAnimationPos();
        openableItem = new OpenableItem();
    }

    public Door(I prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new OpenableAnimationPos();
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
    public OpenableAnimationPos getAnimationPos() {
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
        String message = "open";
        if (collision != null) {
            isOpen = false;
            onOperateActionFailure(collision, message);
        } else {
            onOperateActionSuccess(message);
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem collision = getCollision();
        String message = "closed";
        if (collision != null) {
            isOpen = true;
            onOperateActionFailure(collision, message);
        } else {
            onOperateActionSuccess(message);
        }
    }

    protected void onOperateActionFailure(PosItem collision, String message) {
        animationPos.setOpenableAnimationType(OpenableAnimationType.IDLE);
        controller.getLogger().logItemCannotBeActionBecauseCollides(getName(), message, collision.getName());
    }

    protected void onOperateActionSuccess(String message) {
        animationPos.setOpenableAnimationType(OpenableAnimationType.OPERATING);
        controller.getLogger().logItemAction(getName(), message);
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

        out.writeObject(animationPos);

        out.writeBoolean(isOpen);

        out.writeObject(openableItem);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        animationPos = (OpenableAnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new DoorAnimation(getDir());
        }

        isOpen = in.readBoolean();

        openableItem = (OpenableItem) in.readObject();
    }
}
