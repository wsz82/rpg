package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Container extends Equipment<Container> implements Containable {
    private final List<Equipment> items = new ArrayList<>(0);
    private int filledSpace;

    public Container() {}

    public Container(Container prototype, String name, ItemType type, String path,
                     Boolean visible, Coords pos, Integer level,
                     List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path, visible, pos, level, coverLine, collisionPolygons);
    }

    public boolean add(Equipment e) {
        double size = e.getSize();
        if (filledSpace + size > getSize()) {
            return false;
        }
        setWeight(getWeight() + e.getWeight());
        filledSpace += size;
        items.add(e);
        return true;
    }

    public void remove(Equipment e) {
        items.remove(e);
        setWeight(getWeight() - e.getWeight());
        filledSpace -= e.getSize();
    }

    public void moveItem(Equipment e, Container i) {
        if (i.add(e)) {
            this.remove(e);
        }
    }

    public void open(Creature cr) {
        Controller.get().setCreatureToOpenContainer(cr);
        Controller.get().setContainerToOpen(this);
    }

    @Override
    public List<Equipment> getItems() {
        return items;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(items);

        out.writeInt(filledSpace);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        items.addAll((List<Equipment>) in.readObject());

        filledSpace = in.readInt();
    }
}
