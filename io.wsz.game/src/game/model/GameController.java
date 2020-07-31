package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.save.SavesList;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.setting.Settings;
import game.model.world.GameRunner;
import game.view.launcher.Main;
import game.view.stage.GameStage;
import game.view.stage.GameView;
import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {
    private static GameController singleton;

    private final Controller controller = Controller.get();
    private final AtomicBoolean isGame = new AtomicBoolean(false);
    private final AtomicBoolean isDialog = new AtomicBoolean(false);

    private GameView gameView;
    private GameStage gameStage;
    private GameRunner gameRunner;
    private Creature hoveredHero;

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
        gameStage.setGameViewForCenter();
        gameRunner.resumeGame();
    }

    public void restoreLastPlugin() {
        LastPluginCaretaker pc = new LastPluginCaretaker();
        String lastPluginName = pc.loadMemento(Main.getDir());
        Plugin p = controller.loadPlugin(lastPluginName);
        if (p == null) {
            return;
        }
        if (p.getLocations() != null) {
            controller.setActivePlugin(p);
        }
    }

    public void storeLastPlugin(Plugin p) {
        LastPluginCaretaker pc = new LastPluginCaretaker();
        pc.saveMemento(Main.getDir(), p.getName());
    }


    public SaveMemento loadSaveMemento(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        SaveMemento memento = sc.loadMemento(name);
        controller.getHeroes().addAll(memento.getHeroes());
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

    public void saveGame(boolean overwrite, String name, Coords savedPos, File programDir) {
        if (name == null || name.equals("")) {
            return;
        }
        if (!overwrite) {
            name = createUniqueName(name);
            getSavesList().add(name);
        }
        savedPos.setLocation(controller.getCurrentLocation().getLocation());
        savedPos.level = controller.getCurrentLayer().getLevel();
        SaveMemento memento = new SaveMemento(name, savedPos, controller.getHeroes());
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
        SettingMemento memento = sc.loadMemento();
        Sizes.setFontSize(memento.getFontSize());
        Settings.setGameScrollSpeed(memento.getGameScrollSpeed());
        Settings.setDialogScrollSpeed(memento.getDialogScrollSpeed());
        Settings.setCenterOnPC(memento.isCenterOnPc());
        Settings.setPauseOnInventory(memento.isPauseOnInventory());
        Settings.setResolutionWidth(memento.getResolutionWidth());
        Settings.setResolutionHeight(memento.getResolutionHeight());
        Sizes.setResizeWithResolution(memento.isResizeWithResolution());
        return memento;
    }

    public void saveSettings(File programDir, SettingMemento memento) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        memento.setFontSize(Sizes.getFontSize());
        memento.setGameScrollSpeed(Settings.getGameScrollSpeed());
        memento.setDialogScrollSpeed(Settings.getDialogScrollSpeed());
        memento.setCenterOnPc(Settings.isCenterOnPC());
        memento.setPauseOnInventory(Settings.isPauseOnInventory());
        memento.setResolutionWidth(Settings.getResolutionWidth());
        memento.setResolutionHeight(Settings.getResolutionHeight());
        memento.setResizeWithResolution(Sizes.isResizeWithResolution());
        sc.saveMemento(memento);
    }

    public void loadSaveToLists(SaveMemento m) {
        if (controller.getAssetsList().isEmpty()) {
            controller.loadAssetsToList();
        }

        controller.getLocationsList().clear();
        fillLocationsList(m.getLocations(), true, m.getLastPos());
    }

    public void loadGameActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        controller.getLocationsList().clear();
        controller.getAssetsList().clear();

        Plugin p = ActivePlugin.get().getPlugin();
        controller.getAssetsList().addAll(p.getAssets());
        Coords startPos = p.getStartPos();
        fillLocationsList(p.getLocations(), p.isStartingLocation(), startPos);
    }

    public void fillLocationsList(List<Location> serLocations, boolean isStartingLocation, Coords startPos) {
        controller.fillLocationsList(serLocations);

        if (isStartingLocation) {
            controller.restoreCoordsLocation(startPos);
            Location first = startPos.getLocation();
            controller.getCurrentLocation().setLocation(first);

            int serLevel = startPos.level;
            Optional<Layer> optLayer = first.getLayers().get().stream()
                    .filter(l -> l.getLevel() == serLevel)
                    .findFirst();
            Layer startLayer = optLayer.orElse(null);
            if (startLayer == null) {
                throw new NullPointerException("Start layer \"" + serLevel + "\" does not exist in start location");
            }
            controller.getCurrentLayer().setLayer(startLayer);
        }
    }

    public void initLoadedGameSettings(SaveMemento memento) {
        Coords curPos = controller.getCurPos();
        Coords loadedPos = memento.getLastPos();
        curPos.x = loadedPos.x;
        curPos.y = loadedPos.y;
    }

    public void initNewGameSettings() {
        Plugin p = controller.getActivePlugin();
        double startX = p.getStartPos().x;
        double startY = p.getStartPos().y;
        Coords curPos = controller.getCurPos();
        curPos.x = startX;
        curPos.y = startY;
    }

    public void showLoaderView(Task<String> loader) {
        gameStage.setLoaderViewToCenter(loader);
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

    public GameRunner getGameRunner() {
        return gameRunner;
    }

    public boolean isDialog() {
        return isDialog.get();
    }

    public void setDialog(boolean dialog) {
        isDialog.set(dialog);
    }

    public void endDialog() {
        setDialog(false);
        controller.setAsking(null);
        controller.setAnswering(null);
    }

    public Creature getHoveredHero() {
        return hoveredHero;
    }

    public void setHoveredHero(Creature hoveredHero) {
        this.hoveredHero = hoveredHero;
    }
}