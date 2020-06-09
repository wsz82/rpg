package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.util.List;

public class Teleport extends PosItem {
    private volatile String locationName;
    private volatile Coords exit;
    private volatile int exitLevel;

    public Teleport(String name, ItemType type, String path, Coords pos, int level,
                    Coords[] coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, pos, level, coverLine, collisionPolygons);
    }

    public Teleport(String name, ItemType type, String path, Coords pos, int level,
                    Coords[] coverLine, List<List<Coords>> collisionPolygons,
                    String locationName, Coords exitPos, int exitLevel) {
        super(name, type, path, pos, level, coverLine, collisionPolygons);
        this.locationName = locationName;
        this.exit = exitPos;
        this.exitLevel = exitLevel;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coords getExit() {
        return exit;
    }

    public void setExit(Coords exit) {
        this.exit = exit;
    }

    public int getExitLevel() {
        return exitLevel;
    }

    public void setExitLevel(int exitLevel) {
        this.exitLevel = exitLevel;
    }

    @Override
    public void update() {

    }
}
