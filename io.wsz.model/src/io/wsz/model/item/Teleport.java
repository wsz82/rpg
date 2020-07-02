package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class Teleport extends PosItem<Teleport> {
    private static final long serialVersionUID = 1L;

    private String locationName;
    private Coords exit;
    private Integer exitLevel;

    public Teleport() {}

    public Teleport(Teleport prototype, String name, ItemType type, String path,
                    Boolean visible, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, level,
                coverLine, collisionPolygons);
    }

    public void enter(Creature cr) {
        List<Location> singleLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(getLocationName()))
                .collect(Collectors.toList());
        Location target = singleLocation.get(0);
        if (target == null) {
            return;
        }
        int targetLevel = getExitLevel();
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
            Location from = Controller.get().getCurrentLocation().getLocation();
            cr.changeLocation(from, target, targetLayer, targetX, targetY);
            if (cr.getControl().equals(CONTROL)) {
                Controller.get().setLocationToUpdate(target);
                Controller.get().getCurrentLayer().setLayer(targetLayer);
                Controller.get().setPosToCenter(targetPos);
            }
        }
    }

    public String getIndividualLocationName() {
        return locationName;
    }

    public String getLocationName() {
        if (locationName == null) {
            if (prototype == null) {
                return "";
            }
            return prototype.locationName;
        } else {
            return locationName;
        }
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coords getIndividualExit() {
        return exit;
    }

    public Coords getExit() {
        if (exit == null) {
            if (prototype == null) {
                return new Coords(0, 0);
            }
            return prototype.exit;
        } else {
            return exit;
        }
    }

    public void setExit(Coords exit) {
        if (this.exit == null) {
            this.exit = exit;
        } else {
            this.exit.x = exit.x;
            this.exit.y = exit.y;
        }
    }

    public Integer getIndividualExitLevel() {
        return exitLevel;
    }

    public Integer getExitLevel() {
        if (exitLevel == null) {
            if (prototype == null) {
                return 0;
            }
            return prototype.exitLevel;
        } else {
            return exitLevel;
        }
    }

    public void setExitLevel(Integer exitLevel) {
        this.exitLevel = exitLevel;
    }

    @Override
    public void update() {

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(locationName);

        out.writeObject(exit);

        out.writeObject(exitLevel);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        locationName = (String) in.readObject();

        exit = (Coords) in.readObject();

        exitLevel = (Integer) in.readObject();
    }
}
