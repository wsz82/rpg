package model.plugin;

import java.io.Serializable;
import java.util.List;

class All implements Serializable {
    //TODO versionUID
    private List<LocationSerializable> locations;
    private List<AssetSerializable> assets;

    All(List<LocationSerializable> locations, List<AssetSerializable> assets) {
        this.locations = locations;
        this.assets = assets;
    }

    List<LocationSerializable> getLocations() {
        return locations;
    }

    void setLocations(List<LocationSerializable> locations) {
        this.locations = locations;
    }

    List<AssetSerializable> getAssets() {
        return assets;
    }

    void setAssets(List<AssetSerializable> assets) {
        this.assets = assets;
    }
}
