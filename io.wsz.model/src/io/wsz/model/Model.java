package io.wsz.model;

import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginMetadata;

public class Model {
    private PluginMetadata activePluginMetadata;
    private Plugin activePlugin;
    private final CurrentLocation currentLocation;
    private final CurrentLayer currentLayer;

    public Model() {
        this.currentLocation = new CurrentLocation();
        this.currentLayer = new CurrentLayer();
    }

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

    public CurrentLayer getCurrentLayer() {
        return currentLayer;
    }
}
