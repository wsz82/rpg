package io.wsz.model.effect;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

import java.util.Optional;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class Teleportation {
    private static final ItemType[] landscapeType = new ItemType[] {ItemType.LANDSCAPE};

    public static boolean teleport(Creature cr, Coords exit) {
        if (exit == null || exit.isEmpty()) {
            return false;
        }
        Optional<Location> optLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(exit.getLocation().getName()))
                .findFirst();
        Location target = optLocation.orElse(null);
        if (target == null) {
            return false;
        }
        int targetLevel = exit.level;
        Optional<Layer> optLayer = target.getLayers().get().stream()
                .filter(l -> l.getLevel() == targetLevel)
                .findFirst();
        Layer targetLayer = optLayer.orElse(null);
        if (targetLayer == null) {
            return false;
        }
        double targetX = exit.x;
        double targetWidth = target.getWidth();
        double targetY = exit.y;
        double targetHeight = target.getHeight();
        if (targetX > targetWidth || targetY > targetHeight) {
            return false;
        }
        Board board = Controller.get().getBoard();
        PosItem landscape = board.lookForItem(target, exit.x, exit.y, exit.level, landscapeType, false);
        if (landscape == null) {
            return false;
        }
        PosItem collided = cr.getCollision(exit, target);
        if (collided != null) {
            return false;
        }
        Location from = cr.getPos().getLocation();
        cr.changeLocation(from, exit);
        if (cr.getControl().equals(CONTROL)) {
            Controller.get().setLocationToUpdate(target);
            Controller.get().getCurrentLayer().setLayer(targetLayer);
            Controller.get().setPosToCenter(exit);
        }
        return true;
    }
}
