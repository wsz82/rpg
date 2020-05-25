package model.plugin;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class PluginSerializable implements Serializable {
    //TODO versionUID
    private File file;
    private List<LocationSerializable> locations;
    private List<AssetSerializable> assets;

    public PluginSerializable(){}

    public PluginSerializable(List<LocationSerializable> locations, List<AssetSerializable> assets) {
        this.locations = locations;
        this.assets = assets;
    }

    public PluginSerializable(File file, List<LocationSerializable> locations, List<AssetSerializable> assets) {
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
