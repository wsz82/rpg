package io.wsz.model.animation.creature;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.Creature;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.List;

public class PortraitAnimation extends Animation<Creature> {

    public PortraitAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void play(Creature cr) {
        if (idles.isEmpty()) {
            initIdlesOrEquivalent();
        }
        AnimationPos animationPos = cr.getPortraitAnimationPos();
        ResolutionImage nextIdle = getNextAnimationImage(idles, animationPos, cr.getAnimationSpeed());
        if (nextIdle == null) return;
        cr.setPortrait(nextIdle);
    }

    @Override
    public void initIdlesOrEquivalent() {
        int portraitSize = Sizes.getPortraitSize();
        if (portraitSize == 0) return;
        super.initIdlesOrEquivalent();
    }

    @Override
    protected void initSequenceImage(List<ResolutionImage> sequence, File file) {
        int portraitSize = Sizes.getPortraitSize();
        if (portraitSize == 0) return;
        ResolutionImage image = new ResolutionImage(file, portraitSize, portraitSize);
        sequence.add(image);
    }
}
