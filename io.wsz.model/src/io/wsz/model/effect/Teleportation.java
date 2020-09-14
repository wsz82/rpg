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
    private static final ItemType[] LANDSCAPE_TYPE = new ItemType[] {ItemType.LANDSCAPE};

    public static boolean teleport(Creature cr, Coords exit, Controller controller) {
        if (exit == null || exit.isEmpty()) {
            return false;
        }
        Optional<Location> optLocation = controller.getLocations().stream()
                .filter(l -> l.getId().equals(exit.getLocation().getId()))
                .findFirst();
        Location target = optLocation.orElse(null);
        if (target == null) {
            return false;
        }
        int targetLevel = exit.level;
        Optional<Layer> optLayer = target.getLayers().stream()
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
        Board board = controller.getBoard();
        PosItem landscape = board.lookForItem(target.getItems(), exit.x, exit.y, exit.level, LANDSCAPE_TYPE, false);
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
            controller.setLocationToUpdate(target);
            controller.getCurrentLayer().setLayer(targetLayer);
            controller.setPosToCenter(exit);
        }
        return true;
    }
}
