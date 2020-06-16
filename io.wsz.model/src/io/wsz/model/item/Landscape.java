package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Landscape extends PosItem<Landscape> {

    public Landscape() {}

    public Landscape(Landscape prototype, String name, ItemType type, String path,
                     Boolean visible, Coords pos, Integer level,
                     List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
    }

    @Override
    public void update() {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
    }
}
