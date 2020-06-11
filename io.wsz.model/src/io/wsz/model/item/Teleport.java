package io.wsz.model.item;

import io.wsz.model.stage.Coords;

import java.util.List;

public class Teleport extends PosItem<Teleport> {
    private volatile String locationName;
    private volatile Coords exit;
    private volatile Integer exitLevel;

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

    public String getLocationName() {
        if (locationName == null) {
            return prototype.locationName;
        } else {
            return locationName;
        }
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Coords getExit() {
        if (exit == null) {
            return prototype.exit;
        } else {
            return exit;
        }
    }

    public void setExit(Coords exit) {
        this.exit = exit;
    }

    public Integer getExitLevel() {
        if (exitLevel == null) {
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
