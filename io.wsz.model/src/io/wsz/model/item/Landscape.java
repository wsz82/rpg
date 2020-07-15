package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Landscape extends PosItem<Landscape> {
    private static final long serialVersionUID = 1L;

    public Landscape() {}

    public Landscape(ItemType type) {
        super(type);
    }

    public Landscape(Landscape prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();
    }
}
