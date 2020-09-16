package io.wsz.model.animation;

import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class AnimationPos implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected int frameNumber;
    protected long nextFrameUpdate;
    protected boolean isCycleFinished;
    protected String curIdleAnimation;
    protected String curIdleSequence;
    protected long nextTemporaryIdleUpdate;
    protected boolean isTemporaryIdle;

    public AnimationPos() {
        this.curIdleAnimation = Paths.BASIC;
    }

    public AnimationPos(AnimationPos other) {
        this.curIdleAnimation = other.curIdleAnimation;
        this.curIdleSequence = other.curIdleSequence;
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

    public String getCurIdleSequence() {
        return curIdleSequence;
    }

    public void setCurIdleSequence(String curIdleSequence) {
        this.curIdleSequence = curIdleSequence;
    }

    public int getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }

    public int getNextFrameNumber(int size) {
        if (size == 1) {
            isCycleFinished = true;
            return 0;
        }
        int frameNumber = this.frameNumber;
        int lastNumber = size - 1;
        if (frameNumber > lastNumber) {
            this.frameNumber = 1;
            return 0;
        } else if (frameNumber == lastNumber) {
            isCycleFinished = true;
            this.frameNumber = 0;
            return lastNumber;
        } else if (frameNumber >= 0) {
            isCycleFinished = false;
            this.frameNumber++;
            return frameNumber;
        } else {
            isCycleFinished = false;
            this.frameNumber = 1;
            return 0;
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(curIdleAnimation);

        out.writeObject(curIdleSequence);

        out.writeInt(frameNumber);

        out.writeBoolean(isCycleFinished);

        long menuOpenTime = Sizes.getTimeOfMenuOpen();

        if (nextFrameUpdate > menuOpenTime) {
            out.writeLong(menuOpenTime - nextFrameUpdate);
        } else {
            out.writeLong(0);
        }

        if (nextTemporaryIdleUpdate > menuOpenTime) {
            out.writeLong(menuOpenTime - nextTemporaryIdleUpdate);
        } else {
            out.writeLong(0);
        }

        out.writeBoolean(isTemporaryIdle);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        curIdleAnimation = (String) in.readObject();

        curIdleSequence = (String) in.readObject();

        frameNumber = in.readInt();

        isCycleFinished = in.readBoolean();

        long curTime = System.currentTimeMillis();

        long timeToNextFrameUpdate = in.readLong();
        if (timeToNextFrameUpdate != 0) {
            this.nextFrameUpdate = curTime + timeToNextFrameUpdate;
        }

        long timeToNextTemporaryIdleUpdate = in.readLong();
        if (timeToNextTemporaryIdleUpdate != 0) {
            nextTemporaryIdleUpdate = curTime + timeToNextTemporaryIdleUpdate;
        }

        isTemporaryIdle = in.readBoolean();
    }
}
