package io.wsz.model.plugin;

import io.wsz.model.asset.Asset;
import io.wsz.model.location.Location;

import java.io.File;
import java.util.List;

public class Plugin {
    private File file;
    private List<Location> locations;
    private List<Asset> assets;

    public Plugin(){}

    public Plugin(File file, List<Location> locations, List<Asset> assets) {
        this.file = file;
        this.locations = locations;
        this.assets = assets;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
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
}
