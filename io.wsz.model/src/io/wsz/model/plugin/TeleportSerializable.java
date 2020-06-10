package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class TeleportSerializable extends PosItemSerializable {
    private String locationName;
    private Coords exit;
    private int exitLevel;

    public TeleportSerializable(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                                List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                                String locationName, Coords exit, int exitLevel) {
        super(name, type, path, pos, level, generic, coverLine, collisionPolygons);
        this.locationName = locationName;
        this.exit = exit;
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
}
