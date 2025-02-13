package io.wsz.model.plugin;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Plugin implements Externalizable {
    private static final long serialVersionUID = 1L;

    private Coords startPos;
    private World world;

    public Plugin() {}

    public Coords getStartPos() {
        return startPos;
    }

    public void setStartPos(Coords startPos) {
        this.startPos = startPos;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(startPos);

        out.writeObject(world);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        startPos = (Coords) in.readObject();

        world = (World) in.readObject();
    }
}
