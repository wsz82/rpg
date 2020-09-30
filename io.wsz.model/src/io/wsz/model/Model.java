package io.wsz.model;

import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginMetadata;

public class Model {
    private PluginMetadata activePluginMetadata;
    private Plugin activePlugin;
    private Location currentLocation;
    private Layer currentLayer;

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

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Layer getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(Layer currentLayer) {
        this.currentLayer = currentLayer;
    }
}
