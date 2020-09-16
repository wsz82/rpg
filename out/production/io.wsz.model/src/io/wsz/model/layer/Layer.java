package io.wsz.model.layer;

import io.wsz.model.sizes.Sizes;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Layer implements Externalizable {
    private static final long serialVersionUID = 1L;

    private int level;
    private String id;
    private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");

    public Layer() {}

    public Layer(String layerName) {
        this.level = 0;
        this.id = layerName;
        this.visible.set(true);
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setVisible(boolean value) {
        visible.set(value);
    }

    public boolean getVisible() {
        return visible.get();
    }

    public BooleanProperty getVisibleProperty() {
        return visible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return level == layer.level &&
                id.equals(layer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, id);
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeInt(level);

        out.writeUTF(id);

        out.writeBoolean(visible.get());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        level = in.readInt();

        id = in.readUTF();

        visible.set(in.readBoolean());
    }
}
