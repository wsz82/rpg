package io.wsz.model.animation;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class CreatureIdleAnimationFrames {
    private final List<List<Image>> idleSequences = new ArrayList<>(0);
    private Image main;

    public List<List<Image>> getIdleSequences() {
        return idleSequences;
    }

    public Image getMain() {
        return main;
    }

    public void setMain(Image main) {
        this.main = main;
    }
}
