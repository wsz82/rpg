package io.wsz.model.animation.door;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.openable.OpenableAnimation;
import io.wsz.model.animation.openable.OpenableAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.item.Door;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;

import static io.wsz.model.sizes.Paths.BASIC;
import static io.wsz.model.sizes.Paths.BASIC_OPEN;

public class DoorAnimation extends Animation<Door<?>> {
    private final OpenableAnimation<?> openableAnimation = new OpenableAnimation<>(animationDir);

    public DoorAnimation(String animationDir) {
        super(animationDir);
    }

    @Override
    public void initOtherAnimations(File framesDir, String fileName) {
        super.initOtherAnimations(framesDir, fileName);
        openableAnimation.initOtherAnimations(framesDir, fileName);
    }

    @Override
    public void play(Door d) {
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

    private ResolutionImage getIdle(Door d, OpenableAnimationPos animationPos) {
        return getNextIdle(animationPos, d.getAnimationSpeed());
    }

    private ResolutionImage getOperating(Door d, OpenableAnimationPos animationPos) {
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
