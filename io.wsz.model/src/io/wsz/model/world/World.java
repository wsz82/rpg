package io.wsz.model.world;

import io.wsz.model.asset.Asset;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class World implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Location> locations;
    private List<Asset> assets;

    public World() {}

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(locations);

        out.writeObject(assets);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        locations = (List<Location>) in.readObject();

        assets = (List<Asset>) in.readObject();
    }
}
