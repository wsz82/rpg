package model;

import model.asset.AssetsList;
import model.content.Content;
import model.layer.CurrentLayer;
import model.layer.Layer;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.plugin.*;

import java.io.File;
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
        CurrentLayer.get().setCurrentLayer(layer);
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
        return ActivePlugin.get().getActivePlugin();
    }

    public void loadAssetsToList() {
        Plugin plugin = ActivePlugin.get().getActivePlugin();

        AssetsList.get().clear();
        AssetsList.get().setAll(plugin.getAssets());
    }

    public void loadActivePluginToLists() {
        Plugin plugin = ActivePlugin.get().getActivePlugin();

        LocationsList.get().clear();
        AssetsList.get().clear();

        Location first = plugin.getLocations().get(0);    //TODO change to starting location
        CurrentLocation.get().setLocation(first);
        CurrentLayer.get().setCurrentLayer(first.getLayers().get().get(0));

        AssetsList.get().setAll(plugin.getAssets());
        LocationsList.get().setAll(plugin.getLocations());
    }

    public Plugin loadPlugin(File pluginDir) {
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginDir);
    }

    public void removeContent(Content content) {
        CurrentLocation.get().getContent().remove(content);
    }

    public Layer getCurrentLayer() {
        return CurrentLayer.get().getCurrentLayer();
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
}
