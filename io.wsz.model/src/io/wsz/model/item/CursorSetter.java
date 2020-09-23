package io.wsz.model.item;

import io.wsz.model.animation.cursor.CursorType;

@FunctionalInterface
public interface CursorSetter {

    void set(CursorType type);

}
