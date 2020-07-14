package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OutDoor extends Door<OutDoor> {
    private static final long serialVersionUID = 1L;

    private Coords exit;

    public OutDoor() {}

    public OutDoor(OutDoor prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

    public void enter(Creature cr) {
        Teleportation.teleport(cr, getExit());
    }

    public Coords getIndividualExit() {
        return exit;
    }

    public Coords getExit() {
        if (exit == null) {
            if (prototype == null) {
                return new Coords(0, 0, 0, null);
            }
            return prototype.exit;
        } else {
            return exit;
        }
    }

    public void setExit(Coords exit) {
        this.exit = exit;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(exit);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        exit = (Coords) in.readObject();
    }
}
