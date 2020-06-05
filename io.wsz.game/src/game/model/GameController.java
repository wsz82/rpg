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
import io.wsz.model.item.AssetsList;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.LocationSerializable;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.SerializableConverter;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class GameController {
    private static GameController singleton;
    private volatile boolean isGame; //TODO make atomic?

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
        LastPluginCaretaker pc = new LastPluginCaretaker();
        File lastPluginDir = pc.loadMemento(Main.getDir());
        Plugin p = Controller.get().loadPlugin(lastPluginDir);
        if (p == null) {
            return;
        }
        if (p.getLocations() != null) {
            Controller.get().setActivePlugin(p);
        }
    }

    public void storeLastPlugin(Plugin p) {
        LastPluginCaretaker pc = new LastPluginCaretaker();
        pc.saveMemento(Main.getDir(), p.getFile());
    }


    public SaveMemento loadSaveMemento(String name, File programDir) {
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
        String currentLocationName = Controller.get().getCurrentLocation().getName();
        int currentLayer = Controller.get().getCurrentLayer().getLevel();
        SaveMemento memento = new SaveMemento(name, hvalue, vvalue, currentLocationName, currentLayer);
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

    public void loadSaveToLists(SaveMemento m) {
        if (AssetsList.get().isEmpty()) {
            Controller.get().loadAssetsToList();
        }

        Controller.get().getLocationsList().clear();

        List<LocationSerializable> lsList = m.getLocations();
        List<Location> locations = SerializableConverter.toLocation(lsList, AssetsList.get());
        Controller.get().getLocationsList().setAll(locations);

        List<Location> singleLocation = locations.stream()
                .filter(l -> l.getName().equals(m.getCurrentLocationName()))
                .collect(Collectors.toList());
        Location currentLocation = singleLocation.get(0);
        Controller.get().getCurrentLocation().setLocation(currentLocation);

        List<Layer> singleLayer = currentLocation.getLayers().get().stream()
                .filter(l -> l.getLevel() == m.getCurrentLayer())
                .collect(Collectors.toList());
        Layer layer = singleLayer.get(0);
        Controller.get().getCurrentLayer().setLayer(layer);
    }

    public void loadGameActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        Controller.get().getLocationsList().clear();
        Controller.get().getAssetsList().clear();

        Plugin p = ActivePlugin.get().getPlugin();

        if (p.isStartingLocation()) {
            List<Location> startLocation = p.getLocations().stream()
                    .filter(l -> l.getName().equals(p.getStartLocation()))
                    .collect(Collectors.toList());
            Location first = startLocation.get(0);
            Controller.get().getCurrentLocation().setLocation(first);
            List<Layer> startLayer = first.getLayers().get().stream()
                    .filter(l -> l.getLevel() == p.getStartLayer())
                    .collect(Collectors.toList());
            CurrentLayer.get().setLayer(startLayer.get(0));
        }

        Controller.get().getAssetsList().setAll(p.getAssets());
        Controller.get().getLocationsList().setAll(p.getLocations());
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

    public void initLoadedGameSettings(SaveMemento memento) {
        ScrollPane gameScrollPane = getScrollPane();
        gameScrollPane.setHvalue(memento.gethValue());
        gameScrollPane.setVvalue(memento.getvValue());
    }

    public void initNewGameSettings() {
        Plugin p = Controller.get().getActivePlugin();
        int startX = p.getStartX();
        int startY = p.getStartY();
        double width = Controller.get().getCurrentLocation().getWidth();
        double height = Controller.get().getCurrentLocation().getHeight();
        ScrollPane gameScrollPane = getScrollPane();
        gameScrollPane.setHvalue((double)startX/width);
        gameScrollPane.setVvalue((double)startY/height);
    }

    public void showGame() {
        getGameCanvas().refresh();
        getScrollPane().updatePos();
    }

    public void focusGameScrollPane() {
        getScrollPane().requestFocus();
        getScrollPane().setFocusTraversable(false);
    }
}
