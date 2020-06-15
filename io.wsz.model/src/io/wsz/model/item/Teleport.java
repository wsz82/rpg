package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;

public class Teleport extends PosItem<Teleport> {
    private String locationName;
    private Coords exit;
    private Integer exitLevel;

    public Teleport(Teleport prototype, String name, ItemType type, String path,
                    Boolean visible, Coords pos, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
    }

    public Teleport(Teleport prototype, String name, ItemType type, String path,
                    Boolean visible, Coords pos, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                    String locationName, Coords exitPos, Integer exitLevel) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
        this.locationName = locationName;
        this.exit = exitPos;
        this.exitLevel = exitLevel;
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
                cr.centerScreenOn(targetPos);
            }
            cr.setDest(null);
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
        this.exit = exit;
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
}
