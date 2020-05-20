package model;

import javafx.collections.ObservableList;
import model.asset.AssetsList;
import model.layer.Layer;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.plugin.ActivePlugin;
import model.plugin.LocationSerializable;
import model.plugin.Plugin;
import model.plugin.SerializableConverter;
import model.save.SaveCaretaker;
import model.save.SaveMemento;
import model.save.SavesList;
import model.stage.CurrentLayer;

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
        LocationsList.get().clear();
        Location location = new Location("new", 800, 600);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        LocationsList.get().add(location);
        CurrentLocation.get().setLocation(location);
        CurrentLayer.get().setCurrentLayer(layer);
        AssetsList.get().clear();
    }

    public void setActivePlugin(Plugin plugin) {
        ActivePlugin.get().setActivePlugin(plugin);
        plugin.load();
    }

    public Plugin getActivePlugin() {
        return ActivePlugin.get().getActivePlugin();
    }

    public List<LocationSerializable> getLocationsSerializable() {
        return SerializableConverter.locationsToSerializable(LocationsList.get());
    }

    public SaveMemento loadGameSave(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        SaveMemento memento = sc.loadMemento(name);

        LocationsList.get().clear();
        LocationsList.get().setAll(
                SerializableConverter.toLocationObjects(
                        memento.getLocations(), AssetsList.get()));
        Location first = LocationsList.get().get(0);       //TODO later change to Player current location
        CurrentLocation.get().setLocation(first);
        return memento;
    }

    public void deleteGameSave(String name, File programDir) {
        if (name == null) {
            return;
        }
        SaveCaretaker sc = new SaveCaretaker(programDir);
        sc.deleteSave(name);
        getSavesList().remove(name);
    }

    public void saveGame(boolean overwrite, String name, double hvalue, double vvalue, File programDir) {
        if (name == null || name.equals("")) {
            return;
        }
        if (!overwrite) {
            name = createUniqueName(name);
            getSavesList().add(name);
        }
        SaveMemento memento = new SaveMemento(name, hvalue, vvalue);
        SaveCaretaker sc = new SaveCaretaker(programDir);
        sc.createSave(memento);
    }

    private String createUniqueName(String name) {
        if (getSavesList().contains(name)) {
            name = name + 1;
            createUniqueName(name);
        }
        return name;
    }

    public ObservableList<String> getSavesList() {
        return SavesList.get().getSaves();
    }

    public void initSavesList(File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        List<String> savesNames = sc.getSavesNames();
        getSavesList().addAll(savesNames);
    }
}
