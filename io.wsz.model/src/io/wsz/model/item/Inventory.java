package io.wsz.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private List<Equipment> items = new ArrayList<>(1);
    private Creature owner;
    private int filledSpace;
    private double actualWeight;
    private Weapon equippedWeapon;

    public Inventory() {}

    public Inventory(Creature owner) {
        this.items = new ArrayList<>(1);
        this.owner = owner;
        this.filledSpace = 0;
        this.actualWeight = 0;
    }

    public boolean add(Equipment e) {
        double weight = e.getWeight();
        double size = e.getSize();
        if (actualWeight + weight > getMaxWeight()
                || filledSpace + size > getMaxSize()) {
            return false;
        }
        actualWeight += weight;
        filledSpace += size;
        items.add(e);
        return true;
    }

    public void remove(Equipment e) {
        items.remove(e);
        actualWeight -= e.getWeight();
        filledSpace -= e.getSize();
    }

    public void moveItem(Equipment e, Inventory i) {
        if (i.add(e)) {
            this.remove(e);
        }
    }

    public int getMaxSize() {
        return (owner.getSize().ordinal() + 1) * 5;
    }

    public double getMaxWeight() {
        return owner.getStrength() * 3;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public List<Equipment> getItems() {
        return items;
    }
}
