package io.wsz.model.item;

interface Equippable {
    void onTake(Creature cr);

    void onDrop(Creature cr);

    void onEquip();
}
