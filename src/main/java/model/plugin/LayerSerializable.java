package model.plugin;

import java.io.Serializable;

public class LayerSerializable implements Serializable {
    private int level;
    private String name;
    private boolean visible;

    public LayerSerializable(int level, String name, boolean visible) {
        this.level = level;
        this.name = name;
        this.visible = visible;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
