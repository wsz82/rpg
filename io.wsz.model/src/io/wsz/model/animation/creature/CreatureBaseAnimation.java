package io.wsz.model.animation.creature;

import io.wsz.model.animation.Animation;
import io.wsz.model.item.Creature;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.wsz.model.sizes.Paths.ACTION;

public class CreatureBaseAnimation extends Animation<Creature> {

    private final Map<String, Map<String, List<ResolutionImage>>> actions = new HashMap<>(0);

    public CreatureBaseAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void play(Creature cr) {
        CreatureBaseAnimationPos animationPos = cr.getBaseAnimationPos();
        CreatureBaseAnimationType baseAnimationType = animationPos.getBaseAnimationType();
        ResolutionImage nextImage = switch (baseAnimationType) {
            case IDLE -> getNextAnimationImage(idles, animationPos, cr.getAnimationSpeed());
            case ACTION -> getNextAnimationImage(actions, animationPos, cr.getAnimationSpeed());
        };
        if (nextImage == null) return;
        cr.setBase(nextImage);
    }

    @Override
    public void initOtherAnimations(File animationDir, String fileName) {
        if (fileName.equals(ACTION)) {
            actions.clear();
            initAnimations(animationDir, actions);
        }
    }
}
