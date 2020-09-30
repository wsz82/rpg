package io.wsz.model;

import io.wsz.model.layer.CurrentObservableLayer;
import io.wsz.model.location.CurrentObservableLocation;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginMetadata;

public class Model {
    private PluginMetadata activePluginMetadata;
    private Plugin activePlugin;
    private final CurrentObservableLocation currentObservableLocation;
    private final CurrentObservableLayer currentObservableLayer;

    public Model() {
        this.currentObservableLocation = new CurrentObservableLocation();
        this.currentObservableLayer = new CurrentObservableLayer();
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

    public CurrentObservableLocation getCurrentLocation() {
        return currentObservableLocation;
    }

    public CurrentObservableLayer getCurrentLayer() {
        return currentObservableLayer;
    }
}
