package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class EquipmentType implements Externalizable {
    private static final long serialVersionUID = 1L;

    public static final EquipmentType DEFAULT = new EquipmentType("default");

    private String name;

    public EquipmentType() {}

    public EquipmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = in.readUTF();
    }
}
