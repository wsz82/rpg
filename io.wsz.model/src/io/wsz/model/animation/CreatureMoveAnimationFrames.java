package io.wsz.model.animation;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class CreatureMoveAnimationFrames {
    private final List<Image> moveFrames = new ArrayList<>(0);
    private Image stop;

    public CreatureMoveAnimationFrames() {}

    public List<Image> getMoveFrames() {
        return moveFrames;
    }

    public Image getStop() {
        return stop;
    }

    public void setStop(Image stop) {
        this.stop = stop;
    }
}
