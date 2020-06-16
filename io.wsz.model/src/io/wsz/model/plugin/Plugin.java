package io.wsz.model.plugin;

import io.wsz.model.asset.Asset;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Plugin implements Externalizable {
    private String name;
    private List<Location> locations;
    private List<Asset> assets;
    private boolean active;
    private boolean isStartingLocation;
    private String startLocation;
    private Coords startPos;
    private int startLayer;

    public Plugin() {}

    public Plugin(String name, List<Location> locations, List<Asset> assets) {
        this.name = name;
        this.locations = locations;
        this.assets = assets;
    }

    public Plugin(String name, List<Location> locations, List<Asset> assets, boolean active,
                  boolean isStartingLocation, String startLocation, Coords startPos, int startLayer) {
        this.name = name;
        this.locations = locations;
        this.assets = assets;
        this.active = active;
        this.isStartingLocation = isStartingLocation;
        this.startLocation = startLocation;
        this.startPos = startPos;
        this.startLayer = startLayer;
    }

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
        out.writeUTF(name);

        out.writeObject(locations);

        out.writeObject(assets);

        out.writeBoolean(active);

        out.writeBoolean(isStartingLocation);

        out.writeObject(startLocation);

        out.writeObject(startPos);

        out.writeInt(startLayer);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();

        locations = (List<Location>) in.readObject();

        assets = (List<Asset>) in.readObject();

        active = in.readBoolean();

        isStartingLocation = in.readBoolean();

        startLocation = (String) in.readObject();

        startPos = (Coords) in.readObject();

        startLayer = in.readInt();
    }
}
