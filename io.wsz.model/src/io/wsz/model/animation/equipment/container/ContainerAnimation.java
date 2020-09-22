package io.wsz.model.animation.equipment.container;

import io.wsz.model.animation.equipment.EquipmentAnimation;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.animation.openable.OpenableAnimation;
import io.wsz.model.animation.openable.OpenableAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.item.Container;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;

import static io.wsz.model.sizes.Paths.*;

public class ContainerAnimation extends EquipmentAnimation<Container> {
    private final OpenableAnimation<?> openableAnimation = new OpenableAnimation<>(animationDir, IDLE);

    public ContainerAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void initOtherAnimations(File animationDir, String fileName) {
        super.initOtherAnimations(animationDir, fileName);
        openableAnimation.initOtherAnimations(animationDir, fileName);
    }

    @Override
    public void play(Container c) {
        ContainerAnimationPos animationPos = c.getAnimationPos();
        boolean isOpen = c.isOpen();

        EquipmentAnimationType curEquipmentAnimation = animationPos.getCurAnimation();
        switch (curEquipmentAnimation) {
            case DROP -> playDrop(isOpen, animationPos);
            case INVENTORY -> playInventory(isOpen, animationPos);
        }

        OpenableAnimationPos openableAnimationPos = animationPos.getOpenableAnimationPos();
        OpenableAnimationType openableAnimationType = openableAnimationPos.getOpenableAnimationType();
        ResolutionImage nextFrame = switch (openableAnimationType) {
            case IDLE -> getIdle(c, animationPos);
            case OPERATING -> getOperating(c, openableAnimationPos);
        };

        if (nextFrame == null) return;
        c.setImage(nextFrame);
    }

    private ResolutionImage getIdle(Container c, ContainerAnimationPos animationPos) {
        return getNextAnimationImage(idles, animationPos, c.getAnimationSpeed());
    }

    private ResolutionImage getOperating(Container c, OpenableAnimationPos openableAnimationPos) {
        return openableAnimation.getOperatingImage(c.isOpen(), c.getAnimationSpeed(), openableAnimationPos);
    }

    protected void playDrop(boolean isOpen, ContainerAnimationPos animationPos) {
        animationPos.getOpenableAnimationPos().setCurOperatingAnimation(BASIC);
        if (isOpen) {
            playDropOpen(animationPos);
        } else {
            playDropClosed(animationPos);
        }
    }

    private void playDropClosed(ContainerAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC);
    }

    private void playDropOpen(ContainerAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC_OPEN);
    }

    protected void playInventory(boolean isOpen, ContainerAnimationPos animationPos) {
        animationPos.getOpenableAnimationPos().setCurOperatingAnimation(INVENTORY);
        if (isOpen) {
            playInventoryOpen(animationPos);
        } else {
            playInventoryClosed(animationPos);
        }
    }

    private void playInventoryOpen(ContainerAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(INVENTORY_OPEN);
    }

    private void playInventoryClosed(ContainerAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(INVENTORY);
    }

    public OpenableAnimation<?> getOpenableAnimation() {
        return openableAnimation;
    }
}
