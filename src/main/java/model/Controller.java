package model;

import model.asset.AssetsList;
import model.layer.Layer;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.plugin.ActivePlugin;
import model.plugin.Plugin;
import model.stage.CurrentLayer;

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
        LocationsList.get().clear();
        Location location = new Location("new", 800, 600);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        LocationsList.get().add(location);
        CurrentLocation.get().setLocation(location);
        CurrentLayer.get().setCurrentLayer(layer);
        AssetsList.get().clear();
        Plugin plugin = new Plugin();
        ActivePlugin.get().setActivePlugin(plugin);
    }
}
