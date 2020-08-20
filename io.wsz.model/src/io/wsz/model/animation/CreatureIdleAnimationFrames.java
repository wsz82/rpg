package io.wsz.model.animation;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class CreatureIdleAnimationFrames {
    private final List<List<Image>> idleSequences = new ArrayList<>(0);
    private final List<Image> main = new ArrayList<>(0);

    public CreatureIdleAnimationFrames() {
    }

    public List<List<Image>> getIdleSequences() {
        return idleSequences;
    }

    public List<Image> getMain() {
        return main;
    }
}
