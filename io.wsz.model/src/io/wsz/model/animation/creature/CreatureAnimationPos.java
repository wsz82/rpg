package io.wsz.model.animation.creature;

public class CreatureAnimationPos extends io.wsz.model.animation.AnimationPos {
    private CreatureAnimationType curCreatureAnimationType;
    private MoveDirection moveDirection;
    private long nextPortraitUpdate;
    private long timeToStartPlayIdleAfterStop;

    public CreatureAnimationPos() {
        this.curCreatureAnimationType = CreatureAnimationType.IDLE;
    }

    public CreatureAnimationPos(CreatureAnimationPos other) {
        super(other);
        this.curCreatureAnimationType = other.curCreatureAnimationType;
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

    public MoveDirection getMoveSide() {
        return moveDirection;
    }

    public void setMoveSide(MoveDirection moveDirection) {
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
