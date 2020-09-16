package io.wsz.model.plugin;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PluginMetadata implements Externalizable {
    private static final long serialVersionUID = 1L;

    private transient String pluginName;
    private boolean active;
    private boolean isStartingLocation;

    public PluginMetadata() {}

    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isStartingLocation() {
        return isStartingLocation;
    }

    public void setIsStartingLocation(boolean startingLocation) {
        isStartingLocation = startingLocation;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeBoolean(active);

        out.writeBoolean(isStartingLocation);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        active = in.readBoolean();

        isStartingLocation = in.readBoolean();
    }
}
