package io.wsz.model.plugin;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public class PluginSerializable implements Serializable {
    //TODO versionUID
    private File file;
    private List<LocationSerializable> locations;
    private List<AssetSerializable> assets;
    private boolean isStartingLocation;
    private String startLocation;
    private int startX;
    private int startY;
    private int startLayer;

    public PluginSerializable(){}

    public PluginSerializable(File file, List<LocationSerializable> locations, List<AssetSerializable> assets) {
        this.file = file;
        this.locations = locations;
        this.assets = assets;
    }

    public PluginSerializable(File file, List<LocationSerializable> locations, List<AssetSerializable> assets,
                              boolean isStartingLocation, String startLocation, int startX, int startY, int startLayer) {
        this.file = file;
        this.locations = locations;
        this.assets = assets;
        this.isStartingLocation = isStartingLocation;
        this.startLocation = startLocation;
        this.startX = startX;
        this.startY = startY;
        this.startLayer = startLayer;
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

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getStartLayer() {
        return startLayer;
    }

    public void setStartLayer(int startLayer) {
        this.startLayer = startLayer;
    }
}
