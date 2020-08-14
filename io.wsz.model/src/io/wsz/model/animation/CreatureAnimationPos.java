package io.wsz.model.animation;

public class CreatureAnimationPos {
    private MoveSide moveSide;
    private int frame;
    private long nextFrameUpdate;

    public CreatureAnimationPos() {}

    public MoveSide getMoveSide() {
        return moveSide;
    }

    public void setMoveSide(MoveSide moveSide) {
        this.moveSide = moveSide;
    }

    public int getFrame() {
        return frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public long getNextFrameUpdate() {
        return nextFrameUpdate;
    }

    public void setNextFrameUpdate(long nextFrameUpdate) {
        this.nextFrameUpdate = nextFrameUpdate;
    }
}
