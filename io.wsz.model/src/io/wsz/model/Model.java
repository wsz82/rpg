package io.wsz.model;

import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.plugin.Plugin;

public class Model {
    private Plugin activePlugin;
    private CurrentLocation currentLocation;
    private CurrentLayer currentLayer;

    public Model() {
        currentLocation = new CurrentLocation();
        currentLayer = new CurrentLayer();
    }

    public Plugin getActivePlugin() {
        return activePlugin;
    }

    public void setActivePlugin(Plugin activePlugin) {
        this.activePlugin = activePlugin;
    }

    public CurrentLocation getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(CurrentLocation currentLocation) {
        this.currentLocation = currentLocation;
    }

    public CurrentLayer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(CurrentLayer currentLayer) {
        this.currentLayer = currentLayer;
    }
}
