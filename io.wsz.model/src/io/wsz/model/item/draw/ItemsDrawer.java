package io.wsz.model.item.draw;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;

public class ItemsDrawer {

    private Drawer<Creature> creatureDrawer;
    private Drawer<Equipment<?,?>> equipmentDrawer;

    public Drawer<Creature>  getCreatureDrawer() {
        return creatureDrawer;
    }

    public void setCreatureDrawer(Drawer<Creature>  creatureDrawer) {
        this.creatureDrawer = creatureDrawer;
    }

    public Drawer<Equipment<?,?>> getEquipmentDrawer() {
        return equipmentDrawer;
    }

    public void setEquipmentDrawer(Drawer<Equipment<?,?>> equipmentDrawer) {
        this.equipmentDrawer = equipmentDrawer;
    }
}
