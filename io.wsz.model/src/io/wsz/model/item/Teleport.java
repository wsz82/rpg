package io.wsz.model.item;

import io.wsz.model.effect.Teleportation;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Teleport extends PosItem<Teleport> {
    private static final long serialVersionUID = 1L;

    private Coords exit;
    private List<List<Coords>> teleportCollisionPolygons;

    public Teleport() {}

    public Teleport(ItemType type) {
        super(type);
        this.exit = new Coords();
        this.teleportCollisionPolygons = new ArrayList<>(0);
    }

    public Teleport(Teleport prototype, Boolean visible) {
        super(prototype, visible);
    }

    public void enter(Creature cr) {
        Teleportation.teleport(cr, getExit(), getController());
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

    public List<List<Coords>> getTeleportCollisionPolygons() {
        if (teleportCollisionPolygons == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.teleportCollisionPolygons;
        } else {
            return teleportCollisionPolygons;
        }
    }

    public void setTeleportCollisionPolygons(List<List<Coords>> teleportCollisionPolygons) {
        this.teleportCollisionPolygons = teleportCollisionPolygons;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(exit);

        out.writeObject(teleportCollisionPolygons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        exit = (Coords) in.readObject();

        teleportCollisionPolygons = (List<List<Coords>>) in.readObject();
    }
}
