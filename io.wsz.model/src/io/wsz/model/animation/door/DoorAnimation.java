package io.wsz.model.animation.door;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.openable.OpenableAnimation;
import io.wsz.model.item.Door;
import javafx.scene.image.Image;

import static io.wsz.model.sizes.Paths.BASIC;
import static io.wsz.model.sizes.Paths.BASIC_OPEN;

public class DoorAnimation extends Animation<Door> {
    private final OpenableAnimation openableAnimation = new OpenableAnimation(animationDir, idles, PNG_FILE_FILTER);

    public DoorAnimation(String animationDir) {
        super(animationDir);
    }

    @Override
    public void play(Door d) {
        AnimationPos animationPos = d.getAnimationPos();
        if (d.isOpen()) {
            playOpen(animationPos);
        } else {
            playClosed(animationPos);
        }
        Image nextIdle = getNextIdle(animationPos, 1); //TODO speed
        if (nextIdle == null) return;
        d.setImage(nextIdle);
    }

    private void playClosed(AnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC);
    }

    private void playOpen(AnimationPos animationPos) {
        animationPos.setCurIdleAnimation(BASIC_OPEN);
    }

    public OpenableAnimation getOpenableAnimation() {
        return openableAnimation;
    }
}
