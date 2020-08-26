package io.wsz.model.animation.creature;

import io.wsz.model.sizes.Paths;

public class CreatureAnimationPos extends io.wsz.model.animation.AnimationPos {
    private CreatureAnimationType curCreatureAnimationType;
    private String curMoveAnimation;
    private String moveDirection;
    private long nextPortraitUpdate;
    private long timeToStartPlayIdleAfterStop;

    public CreatureAnimationPos() {
        this.curCreatureAnimationType = CreatureAnimationType.IDLE;
        this.curMoveAnimation = Paths.BASIC;
    }

    public CreatureAnimationPos(CreatureAnimationPos other) {
        super(other);
        this.curCreatureAnimationType = other.curCreatureAnimationType;
        this.curMoveAnimation = other.curMoveAnimation;
        this.moveDirection = other.moveDirection;
        this.nextPortraitUpdate = other.nextPortraitUpdate;
        this.timeToStartPlayIdleAfterStop = other.timeToStartPlayIdleAfterStop;
    }

    public CreatureAnimationType getCurAnimation() {
        return curCreatureAnimationType;
    }

    public void setCurAnimation(CreatureAnimationType curCreatureAnimationType) {
        if (curCreatureAnimationType == this.curCreatureAnimationType) return;
        frameNumber = 0;
        if (curCreatureAnimationType == CreatureAnimationType.MOVE) {
            nextFrameUpdate = 0;
        }
        this.curCreatureAnimationType = curCreatureAnimationType;
    }

    public String getCurMoveAnimation() {
        return curMoveAnimation;
    }

    public void setCurMoveAnimation(String curMoveAnimation) {
        this.curMoveAnimation = curMoveAnimation;
    }

    public String getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(String moveDirection) {
        this.moveDirection = moveDirection;
    }

    public long getNextPortraitUpdate() {
        return nextPortraitUpdate;
    }

    public void setNextPortraitUpdate(long nextPortraitUpdate) {
        this.nextPortraitUpdate = nextPortraitUpdate;
    }

    public long getTimeToStartPlayIdleAfterStop() {
        return timeToStartPlayIdleAfterStop;
    }

    public void setTimeToStartPlayIdleAfterStop(long timeToStartPlayIdleAfterStop) {
        this.timeToStartPlayIdleAfterStop = timeToStartPlayIdleAfterStop;
    }

}
