package model.plugin;

import java.io.Serializable;
import java.util.List;

public class All implements Serializable {
    //TODO versionUID
    private List<LocationSerializable> locations;
    private List<AssetSerializable> assets;

    public All(List<LocationSerializable> locations, List<AssetSerializable> assets) {
        this.locations = locations;
        this.assets = assets;
    }

    public List<LocationSerializable> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationSerializable> locations) {
        this.locations = locations;
    }

    public List<AssetSerializable> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetSerializable> assets) {
        this.assets = assets;
    }
}
