package io.wsz.model;

import io.wsz.model.content.Content;
import io.wsz.model.item.Asset;
import io.wsz.model.item.AssetsList;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.*;
import io.wsz.model.stage.Board;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.util.List;

public class Controller {
    private static Controller singleton;
    private static File programDir;
    private static ScrollPane scrollPane;
    private volatile Location locationToUpdate;

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
        AssetsList.get().setAll(plugin.getAssets());
    }

    public Plugin loadPlugin(String pluginName) {
        if (pluginName == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginName);
    }

    public void removeContent(Content content) {
        CurrentLocation.get().getContent().remove(content);
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

    public ObservableList<Asset> getAssetsList() {
        return AssetsList.get();
    }

    public Board getBoard() {
        return Board.get();
    }

    public static ScrollPane getScrollPane() {
        return scrollPane;
    }

    public static void setScrollPane(ScrollPane scrollPane) {
        Controller.scrollPane = scrollPane;
    }

    public void centerScreenOn(double targetX, double targetY) {    //TODO correct centering
        ScrollPane sp = getScrollPane();
        double width = sp.getWidth();
        double height = sp.getHeight();
        int locationWidth = getCurrentLocation().getWidth();
        int locationHeight = getCurrentLocation().getHeight();
        double screenPosX;
        double screenPosY;
        if (targetX > width/2) {
            screenPosX = targetX + width/2;
        } else {
            screenPosX = targetX - width/2;
        }
        if (targetY > height/2) {
            screenPosY = targetY + height/2;
        } else {
            screenPosY = targetY - height/2;
        }
        double hValue = screenPosX/locationWidth;
        double vValue = screenPosY/locationHeight;
        sp.setHvalue(hValue);
        sp.setVvalue(vValue);
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
}
