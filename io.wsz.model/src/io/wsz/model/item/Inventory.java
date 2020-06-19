package io.wsz.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private List<Equipment> items;
    private Creature owner;
    private int actualSize;
    private double actualWeight;
    private Weapon equippedWeapon;

    public Inventory() {}

    public Inventory(Creature owner) {
        this.items = new ArrayList<>(1);
        this.owner = owner;
        this.actualSize = 0;
        this.actualWeight = 0;
    }

    public boolean add(Equipment e) {
        double weight = e.getWeight();
        double size = e.getSize();
        if (actualWeight + weight > getMaxWeight()
                || actualSize + size > getMaxSize()) {
            return false;
        }
        actualWeight += weight;
        actualSize += size;
        items.add(e);
        return true;
    }

    public void remove(Equipment e) {
        items.remove(e);
        actualWeight -= e.getWeight();
        actualSize -= e.getSize();
    }

    public void moveItem(Equipment e, Inventory i) {
        this.remove(e);
        i.add(e);
    }

    public int getMaxSize() {
        return (owner.getSize().ordinal() + 1) * 5;
    }

    public double getMaxWeight() {
        return owner.getStrength() * 3;
    }

    public List<Equipment> getItems() {
        return items;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }
}
