package io.wsz.model.animation.equipment;

import io.wsz.model.animation.AnimationPos;

public class EquipmentAnimationPos extends AnimationPos {
    protected EquipmentAnimationType curAnimation;

    public EquipmentAnimationPos() {
        this.curAnimation = EquipmentAnimationType.DROP;
    }

    public EquipmentAnimationPos(EquipmentAnimationPos other) {
        super(other);
        this.curAnimation = other.curAnimation;
    }

    public EquipmentAnimationType getCurAnimation() {
        return curAnimation;
    }

    public void setCurAnimation(EquipmentAnimationType curAnimation) {
        if (this.curAnimation == curAnimation) return;
        frameNumber = 0;
        this.curAnimation = curAnimation;
    }
}
