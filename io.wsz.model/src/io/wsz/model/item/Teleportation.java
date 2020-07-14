package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.Optional;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class Teleportation {
    public static void teleport(Creature cr, Coords exit) {
        Optional<Location> optLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(exit.getLocation().getName()))
                .findFirst();
        Location target = optLocation.orElse(null);
        if (target == null) {
            return;
        }
        int targetLevel = exit.level;
        Optional<Layer> optLayer = target.getLayers().get().stream()
                .filter(l -> l.getLevel() == targetLevel)
                .findFirst();
        Layer targetLayer = optLayer.orElse(null);
        if (targetLayer == null) {
            return;
        }
        double targetX = exit.x;
        double targetWidth = target.getWidth();
        double targetY = exit.y;
        double targetHeight = target.getHeight();
        if (targetX < targetWidth && targetY < targetHeight) {
            Location from = cr.getPos().getLocation();
            cr.changeLocation(from, exit);
            if (cr.getControl().equals(CONTROL)) {
                Controller.get().setLocationToUpdate(target);
                Controller.get().getCurrentLayer().setLayer(targetLayer);
                Controller.get().setPosToCenter(exit);
            }
        }
    }
}
