package io.wsz.model.script.command;

import io.wsz.model.item.Containable;

@FunctionalInterface
public interface ItemMover {

    void moveBetween(Containable giving, Containable receiving);

}
