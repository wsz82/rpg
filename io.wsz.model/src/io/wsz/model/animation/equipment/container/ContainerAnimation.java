package io.wsz.model.animation.equipment.container;

import io.wsz.model.animation.equipment.EquipmentAnimation;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.animation.openable.OpenableAnimation;
import io.wsz.model.item.Container;
import io.wsz.model.stage.ResolutionImage;

import static io.wsz.model.sizes.Paths.*;

public class ContainerAnimation extends EquipmentAnimation<Container> {
    private final OpenableAnimation openableAnimation = new OpenableAnimation(animationDir, idles, PNG_FILE_FILTER);

    public ContainerAnimation(String animationDir) {
        super(animationDir);
    }

    @Override
    public void play(Container c) {
        boolean isOpen = c.isOpen();
        EquipmentAnimationPos animationPos = c.getAnimationPos();
        EquipmentAnimationType curAnimation = animationPos.getCurAnimation();
        switch (curAnimation) {
            case DROP -> playDrop(isOpen, animationPos);
            case INVENTORY -> playInventory(isOpen, animationPos);
        }
        ResolutionImage nextIdle = getNextIdle(animationPos, c.getAnimationSpeed());
        if (nextIdle == null) return;
        c.setImage(nextIdle);
    }

    protected void playDrop(boolean isOpen, EquipmentAnimationPos animationPos) {
        if (isOpen) {
            playDropOpen(animationPos);
        } else {
            playDropClosed(animationPos);
        }
    }

    private void playDropClosed(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC);
    }

    private void playDropOpen(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC_OPEN);
    }

    protected void playInventory(boolean isOpen, EquipmentAnimationPos animationPos) {
        if (isOpen) {
            playInventoryOpen(animationPos);
        } else {
            playInventoryClosed(animationPos);
        }
    }

    private void playInventoryOpen(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(INVENTORY_OPEN);
    }

    private void playInventoryClosed(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(INVENTORY);
    }

    public OpenableAnimation getOpenableAnimation() {
        return openableAnimation;
    }
}
