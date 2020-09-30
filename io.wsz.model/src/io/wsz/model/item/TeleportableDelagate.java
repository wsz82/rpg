package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Optional;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class TeleportableDelagate implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final ItemType[] LANDSCAPE_TYPE = new ItemType[] {ItemType.LANDSCAPE};

    private boolean isBeingUsed;

    public boolean teleport(Creature cr, Coords exit, Controller controller) {
        if (isBeingUsed) {
            return false;
        }
        isBeingUsed = true;
        if (exit == null || exit.isEmpty()) {
            isBeingUsed = false;
            return false;
        }
        Optional<Location> optLocation = controller.getLocations().stream()
                .filter(l -> l.getId().equals(exit.getLocation().getId()))
                .findFirst();
        Location target = optLocation.orElse(null);
        if (target == null) {
            isBeingUsed = false;
            return false;
        }
        int targetLevel = exit.level;
        Optional<Layer> optLayer = target.getLayers().stream()
                .filter(l -> l.getLevel() == targetLevel)
                .findFirst();
        Layer targetLayer = optLayer.orElse(null);
        if (targetLayer == null) {
            isBeingUsed = false;
            return false;
        }
        double targetX = exit.x;
        double targetWidth = target.getWidth();
        double targetY = exit.y;
        double targetHeight = target.getHeight();
        if (targetX > targetWidth || targetY > targetHeight) {
            isBeingUsed = false;
            return false;
        }
        Board board = controller.getBoard();
        PosItem landscape = board.lookForItem(target.getItems(), exit.x, exit.y, exit.level, LANDSCAPE_TYPE, false);
        if (landscape == null) {
            isBeingUsed = false;
            return false;
        }
        PosItem collided = cr.getCollision(exit, target);
        if (collided != null) {
            isBeingUsed = false;
            return false;
        }
        Location from = cr.getPos().getLocation();
        cr.changePosition(from, exit);
        if (cr.getControl().equals(CONTROL)) {
            controller.setLocationToUpdate(target);
            controller.setCurrentLayer(targetLayer);
            controller.setPosToCenter(exit);
        }
        boolean isNotTeleportingToTheSameLocation = !from.equals(exit.getLocation());
        if (isNotTeleportingToTheSameLocation) {
            cr.setOnChangeLocationAction(() -> {
                isBeingUsed = false;
            });
        } else {
            isBeingUsed = false;
        }
        return true;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(isBeingUsed);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        isBeingUsed = in.readBoolean();
    }
}
