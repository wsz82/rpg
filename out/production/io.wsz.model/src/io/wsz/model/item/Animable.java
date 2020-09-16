package io.wsz.model.item;

import io.wsz.model.animation.Animation;

public interface Animable{

    <M extends Animation<?>> M getAnimation();
}
