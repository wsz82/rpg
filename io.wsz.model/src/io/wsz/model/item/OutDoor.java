package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.door.DoorAnimation;
import io.wsz.model.effect.Teleportation;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OutDoor extends Door<OutDoor> {
    private static final long serialVersionUID = 1L;

    private Coords exit;
    private OutDoor connection;

    public OutDoor() {}

    public OutDoor(ItemType type, Controller controller) {
        super(type, controller);
        this.exit = new Coords();
    }

    public OutDoor(OutDoor prototype, Boolean visible) {
        super(prototype, visible);
    }

    public boolean enter(Creature cr) {
        return Teleportation.teleport(cr, getExit(), getController());
    }

    public Coords getIndividualExit() {
        return exit;
    }

    public Coords getExit() {
        if (exit == null) {
            if (isThisPrototype()) {
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

    public OutDoor getIndividualConnection() {
        return connection;
    }

    public OutDoor getConnection() {
        if (connection == null) {
            if (isThisPrototype()) {
                return null;
            }
            return prototype.connection;
        }
        return connection;
    }

    public void setConnection(OutDoor connection) {
        this.connection = connection;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            if (isOpen() || isNotOpenable()) {
                return enter(cr);
            } else {
                open();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        if (isNotOpenable()) {
            return false;
        } else {
            return super.creatureSecondaryInteract(cr);
        }
    }

    private boolean isNotOpenable() {
        File programDir = getController().getProgramDir();
        DoorAnimation animation = getAnimation();
        return animation.getOpenableAnimation().isNotOpenable(programDir);
    }

    @Override
    public void open() {
        isOpen = true;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = false;
            System.out.println(getAssetId() + " cannot be open: collides with " + collision.getAssetId());
        } else {
            OutDoor connection = getConnection();
            if (connection == null || this == connection) {
                System.out.println(getAssetId() + " open");
                return;
            }
            connection.setOpen(true);
            PosItem connectionCollision = connection.getCollision();
            if (connectionCollision != null) {
                isOpen = false;
                connection.setOpen(false);
                System.out.println(getAssetId() + " cannot be open: " + connectionCollision.getAssetId() + " blocks behind");
            } else {
                System.out.println(getAssetId() + " open");
            }
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = true;
            System.out.println(getAssetId() + " cannot be closed: collides with " + collision.getAssetId());
        } else {
            OutDoor connection = getConnection();
            if (connection == null || this == connection) {
                System.out.println(getAssetId() + " open");
                return;
            }
            connection.setOpen(false);
            PosItem connectionCollision = connection.getCollision();
            if (connectionCollision != null) {
                isOpen = true;
                connection.setOpen(true);
                System.out.println(getAssetId() + " cannot be closed: " + connectionCollision.getAssetId() + " blocks behind");
            } else {
                System.out.println(getAssetId() + " closed");
            }
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(exit);

        String connectionName;
        if (connection == null) {
            connectionName = "";
        } else {
            connectionName = connection.getAssetId();
        }
        out.writeUTF(connectionName);

        Coords connectionPos;
        if (connection == null) {
            connectionPos = null;
        } else {
            connectionPos = connection.getPos();
        }
        out.writeObject(connectionPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        exit = (Coords) in.readObject();

        String connectionName = in.readUTF();
        Coords connectionPos = (Coords) in.readObject();
        if (!connectionName.isEmpty()) {
            connection = new OutDoor();
            connection.setAssetId(connectionName);
            connection.setPos(connectionPos);
        }
    }
}
