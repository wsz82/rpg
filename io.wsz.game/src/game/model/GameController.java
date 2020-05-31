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
import io.wsz.model.Controller;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.SerializableConverter;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private static GameController singleton;
    private boolean isGame; //TODO make atomic?

    public static GameController get() {
        if (singleton == null) {
            singleton = new GameController();
        }
        return singleton;
    }

    private GameController(){}

    public boolean startGame(SaveMemento memento) {
        if (ActivePlugin.get().getPlugin() == null) {
            return false;
        }
        GameRunner gameRunner = new GameRunner();
        gameRunner.startGame(memento);
        return true;
    }

    public void resumeGame() {
        GameRunner gameRunner = new GameRunner();
        gameRunner.resumeGame();
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

    public void loadGameActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        LocationsList.get().clear();
        AssetsList.get().clear();

        Plugin p = ActivePlugin.get().getPlugin();

        if (p.isStartingLocation()) {
            List<Location> startLocation = p.getLocations().stream()
                    .filter(l -> l.getName().equals(p.getStartLocation()))
                    .collect(Collectors.toList());
            Location first = startLocation.get(0);
            CurrentLocation.get().setLocation(first);
            List<Layer> startLayer = first.getLayers().get().stream()
                    .filter(l -> l.getLevel() == p.getStartLayer())
                    .collect(Collectors.toList());
            CurrentLayer.get().setLayer(startLayer.get(0));
        }

        AssetsList.get().setAll(p.getAssets());
        LocationsList.get().setAll(p.getLocations());
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
