package io.wsz.model.animation;

import javafx.scene.image.Image;

import java.util.List;

public class CreatureAnimationPos {
    private MoveSide moveSide;
    private int moveFrame = -1;
    private long nextMoveUpdate;
    private long nextPortraitUpdate;
    private int idleFrame = -1;
    private List<Image> idleSequence;
    private long nextIdleUpdate;
    private long nextIdleFrameUpdate;
    private long timeToStartPlayIdleAfterStop;

    public CreatureAnimationPos() {}

    public MoveSide getMoveSide() {
        return moveSide;
    }

    public void setMoveSide(MoveSide moveSide) {
        this.moveSide = moveSide;
    }

    public int getMoveFrame() {
        return moveFrame;
    }

    public void setMoveFrame(int moveFrame) {
        this.moveFrame = moveFrame;
    }

    public long getNextMoveUpdate() {
        return nextMoveUpdate;
    }

    public void setNextMoveUpdate(long nextMoveUpdate) {
        this.nextMoveUpdate = nextMoveUpdate;
    }

    public long getNextPortraitUpdate() {
        return nextPortraitUpdate;
    }

    public void setNextPortraitUpdate(long nextPortraitUpdate) {
        this.nextPortraitUpdate = nextPortraitUpdate;
    }

    public int getIdleFrame() {
        return idleFrame;
    }

    public void setIdleFrame(int idleFrame) {
        this.idleFrame = idleFrame;
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

    public long getNextIdleFrameUpdate() {
        return nextIdleFrameUpdate;
    }

    public void setNextIdleFrameUpdate(long nextIdleFrameUpdate) {
        this.nextIdleFrameUpdate = nextIdleFrameUpdate;
    }

    public long getTimeToStartPlayIdleAfterStop() {
        return timeToStartPlayIdleAfterStop;
    }

    public void setTimeToStartPlayIdleAfterStop(long timeToStartPlayIdleAfterStop) {
        this.timeToStartPlayIdleAfterStop = timeToStartPlayIdleAfterStop;
    }
}
