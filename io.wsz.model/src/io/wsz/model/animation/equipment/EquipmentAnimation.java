package io.wsz.model.animation.equipment;

import io.wsz.model.animation.Animation;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;

public abstract class EquipmentAnimation<E extends Equipment> extends Animation<E> {

    public EquipmentAnimation(String animationDir) {
        super(animationDir);
    }

    @Override
    public void play(E e) {
        EquipmentAnimationPos animationPos = e.getAnimationPos();
        EquipmentAnimationType curAnimation = animationPos.getCurAnimation();
        switch (curAnimation) {
            case DROP -> playDrop(animationPos);
            case INVENTORY -> playInventory(animationPos);
        }
        ResolutionImage nextIdle = getNextIdle(animationPos, e.getAnimationSpeed());
        if (nextIdle == null) return;
        e.setImage(nextIdle);
    }

    private void playDrop(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(Paths.BASIC);

    }

    private void playInventory(EquipmentAnimationPos animationPos) {
        animationPos.setCurIdleAnimation(Paths.INVENTORY);
    }
}
