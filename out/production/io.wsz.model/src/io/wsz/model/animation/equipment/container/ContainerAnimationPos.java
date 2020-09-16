package io.wsz.model.animation.equipment.container;

import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationPos;
import io.wsz.model.sizes.Sizes;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ContainerAnimationPos extends EquipmentAnimationPos {
    private static final long serialVersionUID = 1L;

    private OpenableAnimationPos openableAnimationPos;

    public ContainerAnimationPos() {
        this.openableAnimationPos = new OpenableAnimationPos();
    }

    public ContainerAnimationPos(ContainerAnimationPos other) {
        super(other);
        this.openableAnimationPos = other.getOpenableAnimationPos();
    }

    public OpenableAnimationPos getOpenableAnimationPos() {
        return openableAnimationPos;
    }

    public void setOpenableAnimationPos(OpenableAnimationPos openableAnimationPos) {
        this.openableAnimationPos = openableAnimationPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(openableAnimationPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        openableAnimationPos = (OpenableAnimationPos) in.readObject();
    }
}
