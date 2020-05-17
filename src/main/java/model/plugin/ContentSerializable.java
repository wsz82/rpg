package model.plugin;

import java.io.Serializable;

public class ContentSerializable implements Serializable {
    private ItemSerializable item;
    private boolean visible;

    public ContentSerializable(ItemSerializable item, boolean visible) {
        this.item = item;
        this.visible = visible;
    }

    public ItemSerializable getItem() {
        return item;
    }

    public void setItem(ItemSerializable item) {
        this.item = item;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
