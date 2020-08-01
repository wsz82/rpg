package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Cover extends PosItem<Cover> implements Externalizable {
    private static final long serialVersionUID = 1L;

    public Cover() {}

    public Cover(ItemType type) {
        super(type);
    }

    public Cover(Cover prototype, Boolean visible) {
        super(prototype, visible);
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
