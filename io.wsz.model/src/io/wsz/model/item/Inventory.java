package io.wsz.model.item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Inventory implements Serializable {
    private List<Equipment> items;
    private int maxSize;
    private int actualSize;
    private double maxWeight;
    private double actualWeight;

    public Inventory() {}

    public Inventory(int maxSize, double maxWeight) {
        this.items = new ArrayList<>(maxSize);
        this.maxSize = maxSize;
        this.actualSize = 0;
        this.maxWeight = maxWeight;
        this.actualWeight = 0;
    }

    public boolean add(Equipment e) {
        double weight = e.getWeight();
        double size = e.getSize();
        if (actualWeight + weight > maxWeight
                || actualSize + size > maxSize) {
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
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }
}
