package io.wsz.model.item.movement;

import io.wsz.model.item.Equipment;

@FunctionalInterface
public interface InventoryEquipmentMover {

    void move(Equipment<?,?> toMove);

}
