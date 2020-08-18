package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.setting.Settings;
import game.model.world.GameRunner;
import game.view.stage.GameStage;
import game.view.stage.GameView;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {
    private final Controller controller;
    private final ObservableList<String> saves = FXCollections.observableArrayList();

    private final AtomicBoolean isGame = new AtomicBoolean();
    private final AtomicBoolean isDialog = new AtomicBoolean();
    private GameView gameView;
    private GameStage gameStage;
    private GameRunner gameRunner;
    private Creature hoveredHero;

    public GameController(Controller controller) {
        this.controller = controller;
    }

    public boolean startGame(SaveMemento memento) {
        if (controller.getModel().getActivePlugin() == null) {
            return false;
        }
        if (gameRunner == null) {
            gameRunner = new GameRunner(this);
        }
        gameRunner.startGame(memento);
        return true;
    }

    public void resumeGame() {
        gameStage.setGameViewForCenter();
        gameRunner.resumeGame();
    }

    public void restoreLastPlugin() {
        File programDir = controller.getProgramDir();
        LastPluginCaretaker lpc = new LastPluginCaretaker(programDir);
        String lastPluginName = lpc.loadMemento();
        Plugin loadedPlugin = controller.loadPlugin(lastPluginName);
        if (loadedPlugin == null) return;
        if (loadedPlugin.getWorld().getLocations() != null) {
            controller.getModel().setActivePlugin(loadedPlugin);
        }
    }

    public void storeLastPlugin(Plugin p) {
        File programDir = controller.getProgramDir();
        LastPluginCaretaker pc = new LastPluginCaretaker(programDir);
        pc.saveMemento(p.getName());
    }


    public SaveMemento loadSaveMemento(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        SaveMemento memento = sc.loadMemento(name);
        controller.getHeroes().addAll(memento.getHeroes());
        DialogMemento dialogMemento = memento.getDialogMemento();
        if (dialogMemento != null) {
            controller.setDialogMemento(dialogMemento);
            restoreAskingAndAnswering(dialogMemento.getAsking(), dialogMemento.getAnswering());
        }
        return memento;
    }

    private void restoreAskingAndAnswering(PosItem asking, PosItem answering) {
        List<Location> locations = controller.getLocations();
        boolean askingSet = false;
        boolean answeringSet = false;
        for (Location l : locations) {
            for (PosItem pi : l.getItems()) {
                if (!askingSet && asking.equals(pi)) {
                    controller.setAsking(pi);
                    askingSet = true;
                    continue;
                }
                if (!answeringSet && answering.equals(pi)) {
                    controller.setAnswering(pi);
                    answeringSet = true;
                }
            }
        }
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
        if (name == null || name.isEmpty()) {
            return;
        }
        if (!overwrite) {
            name = createUniqueName(name);
            getSavesList().add(name);
        }
        savedPos.setLocation(controller.getCurrentLocation().getLocation());
        savedPos.level = controller.getCurrentLayer().getLevel();
        SaveMemento memento = new SaveMemento(name, savedPos, controller.getHeroes(), controller.getDialogMemento());
        memento.setLocations(controller.getLocations());
        SaveCaretaker sc = new SaveCaretaker(programDir);
        sc.makeSave(memento);
    }

    private String createUniqueName(String name) {
        if (getSavesList().contains(name)) {
            name = name + 1;
            createUniqueName(name);
        }
        return name;
    }

    public ObservableList<String> getSavesList() {
        return saves;
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
        Settings.setResolutionWidth(memento.getResolutionWidth(), controller);
        Settings.setResolutionHeight(memento.getResolutionHeight());
        Sizes.setResizeWithResolution(memento.isResizeWithResolution(), controller);
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

    public void restoreMemento(SaveMemento m) {
        List<Location> locations = m.getLocations();
        controller.getModel().getActivePlugin().getWorld().setLocations(locations);
        controller.restoreItemsCoords(locations);
        restoreStartLocationAndLayer(m.getLastPos());
    }

    public void restoreActivePlugin() {
        Model model = controller.getModel();
        Plugin activePlugin = model.getActivePlugin();
        if (activePlugin == null) {
            return;
        }
        List<Location> locations = activePlugin.getWorld().getLocations();
        controller.restoreItemsCoords(locations);
        Coords startPos = activePlugin.getStartPos();
        restoreStartLocationAndLayer(startPos);
    }

    private void restoreStartLocationAndLayer(Coords startPos) {
        controller.restoreCoordsOfLocation(startPos);
        Location first = startPos.getLocation();
        controller.getCurrentLocation().setLocation(first);

        int serLevel = startPos.level;
        Optional<Layer> optLayer = first.getLayers().stream()
                .filter(l -> l.getLevel() == serLevel)
                .findFirst();
        Layer startLayer = optLayer.orElse(null);
        if (startLayer == null) {
            throw new NullPointerException("Start layer \"" + serLevel + "\" does not exist in start location");
        }
        controller.getCurrentLayer().setLayer(startLayer);
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

    public void setGame(boolean isGame) {
        this.isGame.set(isGame);
    }

    public boolean isDialog() {
        return isDialog.get();
    }

    public void setDialog(boolean isDialog) {
        this.isDialog.set(isDialog);
    }

    public void setGameStage(GameStage gameStage) {
        this.gameStage = gameStage;
    }

    public GameRunner getGameRunner() {
        return gameRunner;
    }

    public void endDialog() {
        setDialog(false);
        controller.setDialogMemento(null);
    }

    public Creature getHoveredHero() {
        return hoveredHero;
    }

    public void setHoveredHero(Creature hoveredHero) {
        this.hoveredHero = hoveredHero;
    }

    public Controller getController() {
        return controller;
    }
}