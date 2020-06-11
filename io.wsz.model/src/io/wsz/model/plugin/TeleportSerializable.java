package io.wsz.model.plugin;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class TeleportSerializable extends PosItemSerializable {
    private String locationName;
    private Coords exit;
    private Integer exitLevel;

    public TeleportSerializable(String prototype, String name, ItemType type, String path, Coords pos, Integer level,
                                List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                                String locationName, Coords exit, Integer exitLevel) {
        super(prototype, name, type, path, pos, level, coverLine, collisionPolygons);
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

    public Integer getExitLevel() {
        return exitLevel;
    }

    public void setExitLevel(Integer exitLevel) {
        this.exitLevel = exitLevel;
    }
}
