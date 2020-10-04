package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.List;

@FunctionalInterface
public interface PolygonsGetter<I extends PosItem<?, ?>> {

    List<List<Coords>> get(I item);

}
