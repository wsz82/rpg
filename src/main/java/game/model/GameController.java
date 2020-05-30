package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.save.SavesList;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.world.GameRunner;
import game.view.launcher.Main;
import game.view.stage.GameCanvas;
import game.view.stage.GameScrollPane;
import javafx.collections.ObservableList;
import model.Controller;
import model.asset.AssetsList;
import model.layer.CurrentLayer;
import model.layer.Layer;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.plugin.Plugin;
import model.plugin.SerializableConverter;

import java.io.File;
import java.util.List;

public class GameController {
    private static GameController singleton;
    private boolean isGame;

    public static GameController get() {
        if (singleton == null) {
            singleton = new GameController();
        }
        return singleton;
    }

    private GameController(){}

    public void startGame(SaveMemento memento) {
        GameRunner gameRunner = new GameRunner();
        gameRunner.startGame(memento);
    }

    public void restoreLastPlugin() {
        LastPluginCaretaker caretaker = new LastPluginCaretaker();
        File lastPluginDir = caretaker.loadMemento(Main.getDir());
        Plugin plugin = Controller.get().loadPlugin(lastPluginDir);
        if (plugin.getLocations() != null) {
            Controller.get().setActivePlugin(plugin);
        }
    }

    public void storeLastPlugin(Plugin plugin) {
        LastPluginCaretaker caretaker = new LastPluginCaretaker();
        caretaker.saveMemento(Main.getDir(), plugin.getFile());
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
        if (AssetsList.get().isEmpty()) {
            Controller.get().loadAssetsToList();
        }

        LocationsList.get().clear();

        LocationsList.get().setAll(
                SerializableConverter.toLocationObjects(
                        memento.getLocations(), AssetsList.get()));

        Location first = LocationsList.get().get(0);       //TODO change to Player current location
        CurrentLocation.get().setLocation(first);
        Layer layer = first.getLayers().get().get(0);
        CurrentLayer.get().setLayer(layer);
    }

    public GameCanvas getGameCanvas() {
        return GameCanvas.get();
    }

    public GameScrollPane getScrollPane() {
        return GameScrollPane.get();
    }

    public boolean isGame() {
        return isGame;
    }

    public void setGame(boolean game) {
        isGame = game;
    }
}
