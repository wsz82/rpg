package io.wsz.model.item;

import io.wsz.model.effect.Teleportation;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OutDoor extends Door<OutDoor> {
    private static final long serialVersionUID = 1L;

    private Coords exit;

    public OutDoor() {}

    public OutDoor(ItemType type) {
        super(type);
        this.exit = new Coords();
    }

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
                return null;
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
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            if (isOpen()) {
                enter(cr);
            } else {
                open();
            }
            return true;
        }
        return false;
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
