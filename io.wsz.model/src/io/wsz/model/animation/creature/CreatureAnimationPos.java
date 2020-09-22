package io.wsz.model.animation.creature;

import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class CreatureAnimationPos extends AnimationPos {
    private static final long serialVersionUID = 1L;

    private CreatureAnimationType curCreatureAnimationType;
    private String curMoveAnimation;
    private String moveDirection;
    private long nextTimeToStartPlayIdleAfterStop;

    public CreatureAnimationPos() {
        this.curCreatureAnimationType = CreatureAnimationType.IDLE;
        this.curMoveAnimation = Paths.BASIC;
    }

    public CreatureAnimationPos(CreatureAnimationPos other) {
        super(other);
        this.curCreatureAnimationType = other.curCreatureAnimationType;
        this.curMoveAnimation = other.curMoveAnimation;
        this.moveDirection = other.moveDirection;
        this.nextTimeToStartPlayIdleAfterStop = other.nextTimeToStartPlayIdleAfterStop;
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

    public long getNextTimeToStartPlayIdleAfterStop() {
        return nextTimeToStartPlayIdleAfterStop;
    }

    public void setNextTimeToStartPlayIdleAfterStop(long nextTimeToStartPlayIdleAfterStop) {
        this.nextTimeToStartPlayIdleAfterStop = nextTimeToStartPlayIdleAfterStop;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(curCreatureAnimationType);

        out.writeObject(curMoveAnimation);

        out.writeObject(moveDirection);

        long menuOpenTime = Sizes.getTimeOfMenuOpen();

        if (nextTimeToStartPlayIdleAfterStop > menuOpenTime) {
            out.writeLong(menuOpenTime - nextTimeToStartPlayIdleAfterStop);
        } else {
            out.writeLong(0);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        curCreatureAnimationType = (CreatureAnimationType) in.readObject();

        curMoveAnimation = (String) in.readObject();

        moveDirection = (String) in.readObject();

        long curTime = System.currentTimeMillis();

        long timeToStartPlayIdleAfterStop = in.readLong();
        if (timeToStartPlayIdleAfterStop != 0) {
            this.nextTimeToStartPlayIdleAfterStop = curTime + timeToStartPlayIdleAfterStop;
        }
    }
}
