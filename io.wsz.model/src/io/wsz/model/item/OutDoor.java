package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.door.DoorAnimation;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.effect.Teleportation;
import io.wsz.model.sizes.Paths;
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

    public OutDoor(Controller controller) {
        super(ItemType.OUTDOOR, controller);
        this.exit = new Coords();
    }

    public OutDoor(OutDoor prototype, Boolean visible) {
        super(prototype, visible);
    }

    @Override
    protected String getAssetDirName() {
        return Paths.OUTDOORS;
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
        String message = "open";
        if (collision != null) {
            isOpen = false;
            onOperateActionFailure(collision, message);
        } else {
            OutDoor connection = getConnection();
            if (connection == null || this == connection) {
                onOperateActionSuccess(message);
                return;
            }
            connection.setOpen(true);
            PosItem connectionCollision = connection.getCollision();
            if (connectionCollision != null) {
                isOpen = false;
                connection.setOpen(false);
                onOperateConnectionActionFailure(connectionCollision, message);
            } else {
                onOperateActionSuccess(message);
            }
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem collision = getCollision();
        String message = "closed";
        if (collision != null) {
            isOpen = true;
            onOperateActionFailure(collision, message);
        } else {
            OutDoor connection = getConnection();
            if (connection == null || this == connection) {
                onOperateActionSuccess(message);
                return;
            }
            connection.setOpen(false);
            PosItem connectionCollision = connection.getCollision();
            if (connectionCollision != null) {
                isOpen = true;
                connection.setOpen(true);
                onOperateConnectionActionFailure(connectionCollision, message);
            } else {
                onOperateActionSuccess(message);
            }
        }
    }

    private void onOperateConnectionActionFailure(PosItem connectionCollision, String message) {
        animationPos.setOpenableAnimationType(OpenableAnimationType.IDLE);
        getController().getLogger().logItemCannotBeActionBecauseIsBlockedBehind(getName(), message, connectionCollision.getName());
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
