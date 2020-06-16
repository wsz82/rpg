package io.wsz.model.item;

import io.wsz.model.stage.Coords;

interface Equippable {
    void onTake(Creature cr);

    void onDrop(Coords pos, Integer level);

    void onEquip();
}
