package io.wsz.model.animation;

import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;

import java.util.ArrayList;
import java.util.List;

public class AnimationPos {
    protected String curIdleAnimation;
    protected List<ResolutionImage> curIdleSequence;
    protected int frameNumber;
    protected boolean isCycleFinished;
    protected long nextFrameUpdate;
    protected long nextTemporaryIdleUpdate;
    protected boolean isTemporaryIdle;

    public AnimationPos() {
        this.curIdleAnimation = Paths.BASIC;
    }

    public AnimationPos(AnimationPos other) {
        this.curIdleAnimation = other.curIdleAnimation;
        List<ResolutionImage> curIdleSequence = other.curIdleSequence;
        if (curIdleSequence == null) {
            this.curIdleSequence = null;
        } else {
            this.curIdleSequence = new ArrayList<>(curIdleSequence);
        }
        this.frameNumber = other.frameNumber;
        this.isCycleFinished = other.isCycleFinished;
        this.nextFrameUpdate = other.nextFrameUpdate;
        this.nextTemporaryIdleUpdate = other.nextTemporaryIdleUpdate;
        this.isTemporaryIdle = other.isTemporaryIdle;
    }


    public String getCurIdleAnimation() {
        return curIdleAnimation;
    }

    public void setCurIdleAnimation(String curIdleAnimation) {
        if (this.curIdleAnimation.equals(curIdleAnimation)) return;
        curIdleSequence = null;
        this.curIdleAnimation = curIdleAnimation;
    }

    public List<ResolutionImage> getCurIdleSequence() {
        return curIdleSequence;
    }

    public void setCurIdleSequence(List<ResolutionImage> curIdleSequence) {
        this.curIdleSequence = curIdleSequence;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
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

    public boolean isCycleFinished() {
        return isCycleFinished;
    }

    public void setCycleFinished(boolean cycleFinished) {
        isCycleFinished = cycleFinished;
    }

    public long getNextFrameUpdate() {
        return nextFrameUpdate;
    }

    public void setNextFrameUpdate(long nextFrameUpdate) {
        this.nextFrameUpdate = nextFrameUpdate;
    }

    public long getNextTemporaryIdleUpdate() {
        return nextTemporaryIdleUpdate;
    }

    public void setNextTemporaryIdleUpdate(long nextTemporaryIdleUpdate) {
        this.nextTemporaryIdleUpdate = nextTemporaryIdleUpdate;
    }

    public boolean isTemporaryIdle() {
        return isTemporaryIdle;
    }

    public void setTemporaryIdle(boolean isTemporaryIdle) {
        if (this.isTemporaryIdle == isTemporaryIdle) return;
        frameNumber = 0;
        this.isTemporaryIdle = isTemporaryIdle;
    }
}
