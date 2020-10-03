package io.wsz.model.item.movement;

import io.wsz.model.item.EquipmentMayCountable;

@FunctionalInterface
public interface InventoryCountableMover {

    void move(EquipmentMayCountable<?,?> toMove);

}
