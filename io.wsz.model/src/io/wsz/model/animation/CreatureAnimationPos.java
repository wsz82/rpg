package io.wsz.model.animation;

import javafx.scene.image.Image;

import java.util.List;

public class CreatureAnimationPos {
    private CreatureAnimationType curCreatureAnimationType = CreatureAnimationType.IDLE;
    private boolean isTemporaryIdle;
    private MoveDirection moveDirection;
    private int frameNumber;
    private long nextFrameUpdate;
    private boolean isCycleFinished;
    private long nextPortraitUpdate;
    private List<Image> idleSequence;
    private long nextIdleUpdate;
    private long timeToStartPlayIdleAfterStop;

    public CreatureAnimationPos() {}

    public CreatureAnimationType getCurAnimation() {
        return curCreatureAnimationType;
    }

    public void setCurAnimation(CreatureAnimationType curCreatureAnimationType) {
        if (curCreatureAnimationType == this.curCreatureAnimationType) return;
        frameNumber = 0;
        this.curCreatureAnimationType = curCreatureAnimationType;
    }

    public boolean isTemporaryIdle() {
        return isTemporaryIdle;
    }

    public void setTemporaryIdle(boolean temporaryIdle) {
        if (isTemporaryIdle == temporaryIdle) return;
        frameNumber = 0;
        isTemporaryIdle = temporaryIdle;
    }

    public MoveDirection getMoveSide() {
        return moveDirection;
    }

    public void setMoveSide(MoveDirection moveDirection) {
        this.moveDirection = moveDirection;
    }

    public boolean isCycleFinished() {
        return isCycleFinished;
    }

    public void setCycleFinished(boolean cycleFinished) {
        isCycleFinished = cycleFinished;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public long getNextFrameUpdate() {
        return nextFrameUpdate;
    }

    public void setNextFrameUpdate(long nextFrameUpdate) {
        this.nextFrameUpdate = nextFrameUpdate;
    }

    public long getNextPortraitUpdate() {
        return nextPortraitUpdate;
    }

    public void setNextPortraitUpdate(long nextPortraitUpdate) {
        this.nextPortraitUpdate = nextPortraitUpdate;
    }

    public List<Image> getIdleSequence() {
        return idleSequence;
    }

    public void setIdleSequence(List<Image> idleSequence) {
        this.idleSequence = idleSequence;
    }

    public long getNextIdleUpdate() {
        return nextIdleUpdate;
    }

    public void setNextIdleUpdate(long nextIdleUpdate) {
        this.nextIdleUpdate = nextIdleUpdate;
    }

    public long getTimeToStartPlayIdleAfterStop() {
        return timeToStartPlayIdleAfterStop;
    }

    public void setTimeToStartPlayIdleAfterStop(long timeToStartPlayIdleAfterStop) {
        this.timeToStartPlayIdleAfterStop = timeToStartPlayIdleAfterStop;
    }

    public int getNextFrameNumber(int framesSize) {
        int frameNumber = this.frameNumber;
        if (frameNumber >= framesSize) {
            isCycleFinished = true;
            this.frameNumber = 1;
            return 0;
        } else {
            isCycleFinished = false;
            this.frameNumber++;
            return frameNumber;
        }
    }

}
