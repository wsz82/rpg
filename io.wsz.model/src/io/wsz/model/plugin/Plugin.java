package io.wsz.model.plugin;

import io.wsz.model.asset.Asset;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Plugin implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String name;
    private boolean active;
    private boolean isStartingLocation;
    private String startLocation;
    private Coords startPos;
    private int startLayer;
    private List<Location> locations;
    private List<Asset> assets;

    public Plugin() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
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

    public void setStartingLocation(boolean startingLocation) {
        isStartingLocation = startingLocation;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public Coords getStartPos() {
        return startPos;
    }

    public void setStartPos(Coords startPos) {
        this.startPos = startPos;
    }

    public int getStartLayer() {
        return startLayer;
    }

    public void setStartLayer(int startLayer) {
        this.startLayer = startLayer;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name);

        out.writeBoolean(active);

        out.writeBoolean(isStartingLocation);

        out.writeObject(startLocation);

        out.writeObject(startPos);

        out.writeInt(startLayer);

        out.writeObject(locations);

        out.writeObject(assets);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = in.readUTF();

        active = in.readBoolean();

        isStartingLocation = in.readBoolean();

        startLocation = (String) in.readObject();

        startPos = (Coords) in.readObject();

        startLayer = in.readInt();

        locations = (List<Location>) in.readObject();

        assets = (List<Asset>) in.readObject();
    }
}
