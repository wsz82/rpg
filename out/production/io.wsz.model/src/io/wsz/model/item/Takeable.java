package io.wsz.model.item;

public interface Takeable {

    boolean tryTake(Creature cr);

    boolean tryDrop(Creature cr, double x, double y);

}
