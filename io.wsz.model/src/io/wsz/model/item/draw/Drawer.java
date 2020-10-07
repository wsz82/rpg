package io.wsz.model.item.draw;

import io.wsz.model.item.PosItem;

@FunctionalInterface
public interface Drawer<P extends PosItem<?,?>> {

    void draw(P item);

}
