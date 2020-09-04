package io.wsz.model.animation.openable;

import io.wsz.model.animation.AnimationPos;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OpenableAnimationPos extends AnimationPos {
    private static final long serialVersionUID = 1L;

    private OpenableAnimationType openableAnimationType;
    private String curOperatingAnimation;
    private String curOperatingSequence;

    public OpenableAnimationPos() {
        this.openableAnimationType = OpenableAnimationType.IDLE;
        this.curOperatingAnimation = Paths.BASIC;
    }

    public OpenableAnimationType getOpenableAnimationType() {
        return openableAnimationType;
    }

    public void setOpenableAnimationType(OpenableAnimationType openableAnimationType) {
        this.openableAnimationType = openableAnimationType;
    }

    public String getCurOperatingAnimation() {
        return curOperatingAnimation;
    }

    public void setCurOperatingAnimation(String curOperatingAnimation) {
        this.curOperatingAnimation = curOperatingAnimation;
    }

    public String getCurOperatingSequence() {
        return curOperatingSequence;
    }

    public void setCurOperatingSequence(String curOperatingSequence) {
        this.curOperatingSequence = curOperatingSequence;
    }

    public int getPreviousFrameNumber(int size) {
        if (size == 1) {
            isCycleFinished = true;
            return 0;
        }
        int frameNumber = this.frameNumber;
        if (frameNumber == 0) {
            isCycleFinished = true;
            this.frameNumber = size - 1;
            return 0;
        } else if (frameNumber > 0 && frameNumber < size) {
            isCycleFinished = false;
            this.frameNumber--;
            return frameNumber;
        } else {
            isCycleFinished = false;
            frameNumber = size - 1;
            this.frameNumber = frameNumber - 1;
            return frameNumber;
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(openableAnimationType);

        out.writeObject(curOperatingAnimation);

        out.writeObject(curOperatingSequence);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        openableAnimationType = (OpenableAnimationType) in.readObject();

        curOperatingAnimation = (String) in.readObject();

        curOperatingSequence = (String) in.readObject();
    }
}
