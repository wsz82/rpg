package io.wsz.model.animation.equipment.countable;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;

public class EquipmentMayCountableAnimation<E extends EquipmentMayCountable<E, EquipmentAnimationPos>>
        extends Animation<EquipmentAnimationPos, E> {

    public EquipmentMayCountableAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void play(E e) {
        EquipmentAnimationPos animationPos = e.getAnimationPos();
        EquipmentAnimationType curAnimation = animationPos.getCurAnimation();
        int amount = e.getAmount();
        switch (curAnimation) {
            case DROP -> playDrop(animationPos, amount);
            case INVENTORY -> playInventory(animationPos, amount);
        }
        ResolutionImage nextIdle = getNextAnimationImage(idles, animationPos, e.getAnimationSpeed());
        if (nextIdle == null) return;
        e.setImage(nextIdle);
    }

    private void playDrop(EquipmentAnimationPos animationPos, int amount) {
        String path;
        if (amount < 2) {
            path = Paths.BASIC;
        } else if (amount < Sizes.MEDIUM_AMOUNT) {
            path = Paths.BASIC_FEW;
        } else {
            path = Paths.BASIC_MANY;
        }
        animationPos.setCurIdleAnimation(path);
    }

    private void playInventory(EquipmentAnimationPos animationPos, int amount) {
        String path;
        if (amount < 2) {
            path = Paths.INVENTORY;
        } else if (amount < Sizes.MEDIUM_AMOUNT) {
            path = Paths.INVENTORY_FEW;
        } else {
            path = Paths.INVENTORY_MANY;
        }
        animationPos.setCurIdleAnimation(path);
    }
}