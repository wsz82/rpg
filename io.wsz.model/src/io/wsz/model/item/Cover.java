package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Cover extends PosItem<Cover> implements Externalizable {
    private static final long serialVersionUID = 1L;

    public Cover() {}

    public Cover(Cover prototype, String name, ItemType type, String path,
                 Boolean visible, Integer level,
                 List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, level,
                coverLine, collisionPolygons);
    }

    @Override
    public void update() {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(serialVersionUID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();
    }
}
