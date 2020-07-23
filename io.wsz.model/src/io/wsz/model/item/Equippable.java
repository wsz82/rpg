package io.wsz.model.item;

interface Equippable {
    boolean onTake(Creature cr, double x, double y);

    boolean onDrop(Creature cr, double x, double y);

    void onEquip();
}
