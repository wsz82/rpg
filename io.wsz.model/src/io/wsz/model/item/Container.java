package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Container extends Equipment<Container> implements Containable {
    private static final long serialVersionUID = 1L;

    private final List<Equipment> items = new ArrayList<>(0);
    private Double nettoWeight;
    private Integer nettoSize;

    public Container() {}

    public Container(Container prototype, String name, ItemType type, String path,
                     Boolean visible, Coords pos, Integer level,
                     List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path, visible, pos, level, coverLine, collisionPolygons);
    }

    public boolean add(Equipment e) {
        double size = e.getSize();
        if (getFilledSpace() + size > getSize() - getNettoSize()) {
            return false;
        }
        items.add(e);
        return true;
    }

    public void remove(Equipment e) {
        items.remove(e);
        setWeight(getWeight() - e.getWeight());
    }

    public void open(Creature cr) {
        Controller.get().setCreatureToOpenContainer(cr);
        Controller.get().setContainerToOpen(this);
    }

    public int getFilledSpace() {
        return getItems().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    public Double getIndividualNettoWeight() {
        return nettoWeight;
    }

    public Double getNettoWeight() {
        if (nettoWeight == null) {
            if (prototype == null) {
                return 0.0;
            }
            return prototype.nettoWeight;
        } else {
            return nettoWeight;
        }
    }

    public void setNettoWeight(Double nettoWeight) {
        this.nettoWeight = nettoWeight;
    }

    public Integer getIndividualNettoSize() {
        return nettoSize;
    }

    public Integer getNettoSize() {
        if (nettoSize == null) {
            if (prototype == null) {
                return 0;
            }
            return prototype.nettoSize;
        } else {
            return nettoSize;
        }
    }

    public void setNettoSize(Integer nettoSize) {
        this.nettoSize = nettoSize;
    }

    @Override
    public Double getWeight() {
        return getNettoWeight() + getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    @Override
    public List<Equipment> getItems() {
        return items;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(items);

        out.writeObject(nettoWeight);

        out.writeObject(nettoSize);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        items.addAll((List<Equipment>) in.readObject());

        nettoWeight = (Double) in.readObject();

        nettoSize = (Integer) in.readObject();
    }
}
