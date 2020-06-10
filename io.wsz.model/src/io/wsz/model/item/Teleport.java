package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;

import java.util.List;

import static io.wsz.model.item.ItemType.CREATURE;

public class Teleport extends PosItem {
    private volatile String locationName;
    private volatile Coords exit;
    private volatile int exitLevel;

    public Teleport(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, pos, level, generic, coverLine, collisionPolygons);
    }

    public Teleport(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                    String locationName, Coords exitPos, int exitLevel) {
        super(name, type, path, pos, level, generic, coverLine, collisionPolygons);
        this.locationName = locationName;
        this.exit = exitPos;
        this.exitLevel = exitLevel;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Teleport) c.getItem()).setLocationName(locationName));
        });
    }

    public Coords getExit() {
        return exit;
    }

    public void setExit(Coords exit) {
        this.exit = exit;
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Teleport) c.getItem()).setExit(exit));
        });
    }

    public int getExitLevel() {
        return exitLevel;
    }

    public void setExitLevel(int exitLevel) {
        this.exitLevel = exitLevel;
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Teleport) c.getItem()).setExitLevel(exitLevel));
        });
    }

    @Override
    public void update() {

    }
}
