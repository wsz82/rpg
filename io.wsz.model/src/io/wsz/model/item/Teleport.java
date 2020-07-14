package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class Teleport extends PosItem<Teleport> {
    private static final long serialVersionUID = 1L;

    private Coords exit;
    private List<List<Coords>> teleportCollisionPolygons;

    public Teleport() {}

    public Teleport(Teleport prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

    public void enter(Creature cr) {
        List<Location> singleLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(getExit().getLocation().getName()))
                .collect(Collectors.toList());
        Location target = singleLocation.get(0);
        if (target == null) {
            return;
        }
        int targetLevel = getExit().level;
        List<Layer> singleLayer = target.getLayers().get().stream()
                .filter(l -> l.getLevel() == targetLevel)
                .collect(Collectors.toList());
        Layer targetLayer = singleLayer.get(0);
        if (targetLayer == null) {
            return;
        }
        Coords targetPos = getExit();
        double targetX = targetPos.x;
        double targetWidth = target.getWidth();
        double targetY = targetPos.y;
        double targetHeight = target.getHeight();
        if (targetX < targetWidth && targetY < targetHeight) {
            Location from = cr.getPos().getLocation();
            cr.changeLocation(from, getExit());
            if (cr.getControl().equals(CONTROL)) {
                Controller.get().setLocationToUpdate(target);
                Controller.get().getCurrentLayer().setLayer(targetLayer);
                Controller.get().setPosToCenter(targetPos);
            }
        }
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
        if (this.exit == null) {
            this.exit = new Coords();
        }
        this.exit.x = exit.x;
        this.exit.y = exit.y;
        this.exit.level = exit.level;
        this.exit.setLocation(exit.getLocation());
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
