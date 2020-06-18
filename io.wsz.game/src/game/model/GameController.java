package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.save.SavesList;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.world.GameRunner;
import game.view.launcher.Main;
import game.view.stage.GameStage;
import game.view.stage.GameView;
import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GameController {
    private static GameController singleton;
    private final AtomicBoolean isGame = new AtomicBoolean(false);
    private GameView gameView;
    private GameStage gameStage;
    private GameRunner gameRunner;

    public static GameController get() {
        if (singleton == null) {
            singleton = new GameController();
        }
        return singleton;
    }

    private GameController() {}

    public boolean startGame(SaveMemento memento) {
        if (ActivePlugin.get().getPlugin() == null) {
            return false;
        }
        if (gameRunner == null) {
            gameRunner = new GameRunner();
        }
        gameRunner.startGame(memento);
        return true;
    }

    public void resumeGame() {
        gameStage.setGameForRoot();
        gameRunner.resumeGame();
    }

    public void openInventory(Creature active) {
        isGame.set(false);
        gameStage.setInventoryForRoot(active);
    }

    public void restoreLastPlugin() {
        LastPluginCaretaker pc = new LastPluginCaretaker();
        String lastPluginName = pc.loadMemento(Main.getDir());
        Plugin p = Controller.get().loadPlugin(lastPluginName);
        if (p == null) {
            return;
        }
        if (p.getLocations() != null) {
            Controller.get().setActivePlugin(p);
        }
    }

    public void storeLastPlugin(Plugin p) {
        LastPluginCaretaker pc = new LastPluginCaretaker();
        pc.saveMemento(Main.getDir(), p.getName());
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

    public void saveGame(boolean overwrite, String name, Coords savedPos, File programDir) {
        if (name == null || name.equals("")) {
            return;
        }
        if (!overwrite) {
            name = createUniqueName(name);
            getSavesList().add(name);
        }
        String currentLocationName = Controller.get().getCurrentLocation().getName();
        int currentLayer = Controller.get().getCurrentLayer().getLevel();
        SaveMemento memento = new SaveMemento(name, savedPos, currentLocationName, currentLayer);
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
        if (Controller.get().getAssetsList().isEmpty()) {
            Controller.get().loadAssetsToList();
        }

        Controller.get().getLocationsList().clear();

        List<Location> locations = m.getLocations();
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

        Controller.get().getAssetsList().addAll(p.getAssets());
        Controller.get().getLocationsList().setAll(p.getLocations());
    }

    public void initLoadedGameSettings(SaveMemento memento) {
        Coords currentPos = gameView.getCurrentPos();
        Coords loadedPos = memento.getLastPos();
        currentPos.x = loadedPos.x;
        currentPos.y = loadedPos.y;
    }

    public void initNewGameSettings() {
        Plugin p = Controller.get().getActivePlugin();
        double startX = p.getStartPos().x;
        double startY = p.getStartPos().y;
        Coords currentPos = gameView.getCurrentPos();
        currentPos.x = startX;
        currentPos.y = startY;
    }

    public void refreshGame() {
        gameView.refresh();
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public boolean isGame() {
        return isGame.get();
    }

    public void setGame(boolean game) {
        isGame.set(game);
    }

    public GameStage getGameStage() {
        return gameStage;
    }

    public void setGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
    }
}