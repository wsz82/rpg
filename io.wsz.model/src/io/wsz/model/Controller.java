package io.wsz.model;

import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.content.Content;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.*;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class Controller {
    private static Controller singleton;

    public static Controller get() {
        if (singleton == null) {
            singleton = new Controller();
        }
        return singleton;
    }

    private Controller(){}

    public void initNewPlugin() {
        AssetsList.get().clear();
        LocationsList.get().clear();
        Location location = new Location("new", 800, 600);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        LocationsList.get().add(location);
        CurrentLocation.get().setLocation(location);
        CurrentLayer.get().setLayer(layer);
    }

    public void loadAndRestorePlugin(File pluginDir) {
        PluginCaretaker pc = new PluginCaretaker();
        setActivePlugin(pc.load(pluginDir));
        loadActivePluginToLists();
    }

    public void setActivePlugin(Plugin plugin) {
        ActivePlugin.get().setActivePlugin(plugin);
    }

    public Plugin getActivePlugin() {
        return ActivePlugin.get().getPlugin();
    }

    public void loadAssetsToList() {
        Plugin plugin = ActivePlugin.get().getPlugin();

        AssetsList.get().clear();
        AssetsList.get().setAll(plugin.getAssets());
    }

    public void loadActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        LocationsList.get().clear();
        AssetsList.get().clear();

        Plugin plugin = ActivePlugin.get().getPlugin();

        Location first = plugin.getLocations().get(0);    //TODO change to starting location
        CurrentLocation.get().setLocation(first);
        CurrentLayer.get().setLayer(first.getLayers().get().get(0));

        AssetsList.get().setAll(plugin.getAssets());
        LocationsList.get().setAll(plugin.getLocations());
    }

    public Plugin loadPlugin(File pluginDir) {
        if (pluginDir == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginDir);
    }

    public void removeContent(Content content) {
        CurrentLocation.get().getContent().remove(content);
    }

    public CurrentLayer getCurrentLayer() {
        return CurrentLayer.get();
    }

    public List<LocationSerializable> getLocationsSerializable() {
        return SerializableConverter.locationsToSerializable(LocationsList.get());
    }

    public CurrentLocation getCurrentLocation() {
        return CurrentLocation.get();
    }

    public void setCurrentLocation(Location location) {
        CurrentLocation.get().setLocation(location);
    }

    public List<Location> getLocationsList() {
        return LocationsList.get();
    }

    public Collection<Asset> getAssetsList() {
        return AssetsList.get();
    }
}
