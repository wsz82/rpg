package io.wsz.model;

import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.*;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;

public class Controller {
    private static Controller singleton;
    private static File programDir;
    private volatile Location locationToUpdate;
    private final ObjectProperty<Coords> centerPos = new SimpleObjectProperty<>();

    public static Controller get() {
        if (singleton == null) {
            singleton = new Controller();
        }
        return singleton;
    }

    public static File getProgramDir() {
        return programDir;
    }

    public static void setProgramDir(File programDir) {
        Controller.programDir = programDir;
    }

    private Controller(){}

    public void setActivePlugin(Plugin plugin) {
        ActivePlugin.get().setPlugin(plugin);
    }

    public Plugin getActivePlugin() {
        return ActivePlugin.get().getPlugin();
    }

    public void loadAssetsToList() {
        Plugin plugin = ActivePlugin.get().getPlugin();

        AssetsList.get().clear();
        AssetsList.get().addAll(plugin.getAssets());
    }

    public Plugin loadPlugin(String pluginName) {
        if (pluginName == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginName);
    }

    public void removeItem(PosItem pi) {
        CurrentLocation.get().getItems().remove(pi);
    }

    public CurrentLayer getCurrentLayer() {
        return CurrentLayer.get();
    }

    public List<LocationSerializable> getLocationsSerializable() {
        return SerializableConverter.toSerializableLocations(LocationsList.get());
    }

    public CurrentLocation getCurrentLocation() {
        return CurrentLocation.get();
    }

    public ObservableList<Location> getLocationsList() {
        return LocationsList.get();
    }

    public List<Asset> getAssetsList() {
        return AssetsList.get();
    }

    public Board getBoard() {
        return Board.get();
    }

    public Coords getCenterPos() {
        return centerPos.get();
    }

    public ObjectProperty<Coords> centerPosProperty() {
        return centerPos;
    }

    public void setCenterPos(Coords centerPos) {
        this.centerPos.set(centerPos);
    }

    public Plugin loadPluginMetadata(String name) {
        PluginCaretaker pc = new PluginCaretaker();
        return pc.getPluginMetadata(name);
    }


    public Location getUpdatedLocation() {
        return locationToUpdate;
    }

    public void setUpdatedLocation(Location locationToUpdate) {
        this.locationToUpdate = locationToUpdate;
    }

    public Coords getBoardPos() {
        return Board.get().getBoardPos();
    }
}
