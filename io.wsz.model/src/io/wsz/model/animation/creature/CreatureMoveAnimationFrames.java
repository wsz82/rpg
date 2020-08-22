package io.wsz.model.animation.creature;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class CreatureMoveAnimationFrames {
    private final List<Image> walkEmptyFrames = new ArrayList<>(0);
    private Image emptyStop;

    public CreatureMoveAnimationFrames() {}

    public List<Image> getWalkEmptyFrames() {
        return walkEmptyFrames;
    }

    public Image getEmptyStop() {
        return emptyStop;
    }

    public void setEmptyStop(Image emptyStop) {
        this.emptyStop = emptyStop;
    }
}
