package io.wsz.model.item;

interface Equippable {
    void onTake(Creature cr, double x, double y);

    boolean onDrop(Creature cr, double x, double y);

    void onEquip();
}
