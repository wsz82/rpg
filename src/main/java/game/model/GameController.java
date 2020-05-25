package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.save.SavesList;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.world.World;
import game.view.launcher.Main;
import game.view.stage.GameBoard;
import javafx.collections.ObservableList;
import model.Controller;
import model.asset.AssetsList;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.plugin.Plugin;
import model.plugin.SerializableConverter;

import java.io.File;
import java.util.List;

public class GameController {
    private static GameController singleton;

    public static GameController get() {
        if (singleton == null) {
            singleton = new GameController();
        }
        return singleton;
    }

    private GameController(){}

    public void startGame(SaveMemento memento) {
        World world = new World();
        world.startGame(memento);
    }

    public void restoreLastPlugin() {
        LastPluginCaretaker caretaker = new LastPluginCaretaker();
        Plugin plugin = caretaker.loadMemento(Main.getDir());
        if (plugin.getLocations() != null) {
            Controller.get().setActivePlugin(plugin);
        }
    }

    public void storeLastPlugin(Plugin plugin) {
        LastPluginCaretaker caretaker = new LastPluginCaretaker();
        caretaker.saveMemento(Main.getDir(), plugin);
    }


    public SaveMemento loadGameSave(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        return sc.loadMemento(name);
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

    public SettingMemento loadSettings(File programDir) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        return sc.loadMemento();
    }

    public void saveSettings(File programDir, SettingMemento memento) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        sc.saveMemento(memento);
    }

    public void loadSaveToLists(SaveMemento memento) {
        LocationsList.get().clear();

        LocationsList.get().setAll(
                SerializableConverter.toLocationObjects(
                        memento.getLocations(), AssetsList.get()));

        Location first = LocationsList.get().get(0);       //TODO change to Player current location
        CurrentLocation.get().setLocation(first);
    }

    public GameBoard getBoard() {
        return GameBoard.get();
    }
}
