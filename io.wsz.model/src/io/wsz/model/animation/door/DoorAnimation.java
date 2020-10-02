package io.wsz.model.animation.door;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.openable.OpenableAnimation;
import io.wsz.model.animation.openable.OpenableAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.item.Door;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;

import static io.wsz.model.sizes.Paths.*;

public class DoorAnimation<D extends Door<D>> extends Animation<OpenableAnimationPos, D> {
    private final OpenableAnimation<D> openableAnimation = new OpenableAnimation<>(animationDir, IDLE);

    public DoorAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void initOtherAnimations(File animationDir, String fileName) {
        super.initOtherAnimations(animationDir, fileName);
        openableAnimation.initOtherAnimations(animationDir, fileName);
    }

    @Override
    public void play(D d) {
        OpenableAnimationPos animationPos = d.getAnimationPos();
        if (d.isOpen()) {
            playOpen(animationPos);
        } else {
            playClosed(animationPos);
        }

        OpenableAnimationType openableAnimationType = animationPos.getOpenableAnimationType();
        ResolutionImage nextFrame = switch (openableAnimationType) {
            case IDLE -> getIdle(d, animationPos);
            case OPERATING -> getOperating(d, animationPos);
        };

        if (nextFrame == null) return;
        d.setImage(nextFrame);
    }

    private ResolutionImage getIdle(D d, OpenableAnimationPos animationPos) {
        return getNextAnimationImage(idles, animationPos, d.getAnimationSpeed());
    }

    private ResolutionImage getOperating(D d, OpenableAnimationPos animationPos) {
        return openableAnimation.getOperatingImage(d.isOpen(), d.getAnimationSpeed(), animationPos);
    }

    private void playClosed(AnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC);
    }

    private void playOpen(AnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC_OPEN);
    }

    public OpenableAnimation<?> getOpenableAnimation() {
        return openableAnimation;
    }
}
