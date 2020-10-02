package io.wsz.model.item;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;

public interface Animable<A extends AnimationPos, I extends PosItem<I, A>> {

    <M extends Animation<A, I>> M getAnimation();
}
