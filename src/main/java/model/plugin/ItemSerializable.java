package model.plugin;

import java.io.Serializable;

public class ItemSerializable implements Serializable {
    private String name;
    private CoordinatesSerializable pos;
    private int level;

    public ItemSerializable(String name, CoordinatesSerializable pos, int level) {
        this.name = name;
        this.pos = pos;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordinatesSerializable getPos() {
        return pos;
    }

    public void setPos(CoordinatesSerializable pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
