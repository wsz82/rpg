package io.wsz.model.plugin;

import java.io.Serializable;

public class ContentSerializable implements Serializable {
    private PosItemSerializable item;
    private boolean visible;

    public ContentSerializable(PosItemSerializable item, boolean visible) {
        this.item = item;
        this.visible = visible;
    }

    public PosItemSerializable getItem() {
        return item;
    }

    public void setItem(PosItemSerializable item) {
        this.item = item;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
