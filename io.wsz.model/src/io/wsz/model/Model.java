package io.wsz.model;

import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginMetadata;

public class Model {
    private PluginMetadata activePluginMetadata;
    private Plugin activePlugin;
    private CurrentLocation currentLocation;
    private CurrentLayer currentLayer;

    public Model() {}

    public Plugin getActivePlugin() {
        return activePlugin;
    }

    public void setActivePlugin(Plugin activePlugin) {
        this.activePlugin = activePlugin;
    }

    public PluginMetadata getActivePluginMetadata() {
        return activePluginMetadata;
    }

    public void setActivePluginMetadata(PluginMetadata activePluginMetadata) {
        this.activePluginMetadata = activePluginMetadata;
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
