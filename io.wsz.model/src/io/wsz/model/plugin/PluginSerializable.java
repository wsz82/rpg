package io.wsz.model.plugin;

import java.io.Serializable;
import java.util.List;

public class PluginSerializable implements Serializable {
    //TODO versionUID
    private String name;
    private List<LocationSerializable> locations;
    private List<AssetSerializable> assets;
    private boolean active;
    private boolean isStartingLocation;
    private String startLocation;
    private int startX;
    private int startY;
    private int startLayer;

    public PluginSerializable(){}

    public PluginSerializable(String name, List<LocationSerializable> locations, List<AssetSerializable> assets) {
        this.name = name;
        this.locations = locations;
        this.assets = assets;
    }

    public PluginSerializable(String name, List<LocationSerializable> locations, List<AssetSerializable> assets, boolean active,
                              boolean isStartingLocation, String startLocation, int startX, int startY, int startLayer) {
        this.name = name;
        this.locations = locations;
        this.assets = assets;
        this.active = active;
        this.isStartingLocation = isStartingLocation;
        this.startLocation = startLocation;
        this.startX = startX;
        this.startY = startY;
        this.startLayer = startLayer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
