package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.setting.Settings;
import game.model.world.GameRunner;
import game.view.menu.GameStage;
import game.view.world.board.GameView;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Creature;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.plugin.PluginMetadata;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {
    private final Controller controller;
    private final ObservableList<String> saves = FXCollections.observableArrayList();
    private final AtomicBoolean isGame = new AtomicBoolean();
    private final AtomicBoolean isDialog = new AtomicBoolean();

    private Settings settings;
    private GameView gameView;
    private GameStage gameStage;
    private GameRunner gameRunner;
    private Creature hoveredHero;

    public GameController(Controller controller) {
        this.controller = controller;
    }

    public boolean startGame(SaveMemento memento) {
        if (controller.getModel().getActivePluginMetadata() == null) {
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

    public void restoreLastPluginMetadata() {
        File programDir = controller.getProgramDir();
        LastPluginCaretaker lpc = new LastPluginCaretaker(programDir);
        String lastPluginName = lpc.loadMemento();
        PluginMetadata loadedMetadata = controller.loadPluginMetadata(lastPluginName);
        if (loadedMetadata == null) return;
        controller.getModel().setActivePluginMetadata(loadedMetadata);
    }

    public void storeLastPluginName(PluginMetadata metadata) {
        File programDir = controller.getProgramDir();
        LastPluginCaretaker pc = new LastPluginCaretaker(programDir);
        String pluginName = metadata.getPluginName();
        pc.saveMemento(pluginName);
    }


    public SaveMemento loadSaveMemento(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        SaveMemento memento = sc.loadMemento(name);
        controller.getHeroes().addAll(memento.getHeroes());
        DialogMemento dialogMemento = memento.getDialogMemento();
        if (dialogMemento != null) {
            controller.setDialogMemento(dialogMemento);
            restoreAskingAndAnswering(dialogMemento.getPc(), dialogMemento.getNpc());
        }
        return memento;
    }

    private void restoreAskingAndAnswering(Creature asking, PosItem answering) {
        List<Location> locations = controller.getLocations();
        boolean askingSet = false;
        boolean answeringSet = false;
        for (Location l : locations) {
            for (PosItem pi : l.getItems()) {
                if (!askingSet && asking.equals(pi)) {
                    Creature cr = (Creature) pi;
                    controller.setDialogNpc(cr);
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

    public SettingMemento loadSettingsMemento(File programDir) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        return sc.loadMemento();
    }

    public void restoreSettings(SettingMemento memento) {
        Sizes.setResizeWithResolution(memento.isResizeWithResolution(), controller);
        setLocale(settings.getLocale());
    }

    public void setLocale(String locale) {
        if (locale == null || locale.isEmpty()) return;
        Properties localeProperties = new Properties();
        File programDir = controller.getProgramDir();
        String localePath = programDir + Paths.LOCALE_DIR + File.separator + locale + Paths.DOT_PROPERTIES;
        FileInputStream in = null;
        try {
            in = new FileInputStream(localePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (in == null) return;
        try {
            localeProperties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller.setLocale(localeProperties);
    }

    public void saveSettings(File programDir, SettingMemento memento) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        memento.setResizeWithResolution(Sizes.isResizeWithResolution());
        memento.setSettings(settings);
        sc.saveMemento(memento);
    }

    public void restoreSaveMemento(SaveMemento m) {
        List<Location> locations = m.getLocations();

        Model model = controller.getModel();
        Plugin activePlugin = model.getActivePlugin();
        if (activePlugin == null) {
            activePlugin = getLoadedPluginFromMetadata(model);
            model.setActivePlugin(activePlugin);
        }
        if (activePlugin == null) return;
        World world = activePlugin.getWorld();
        world.setLocations(locations);

        Coords lastPos = m.getLastPos();
        restorePluginReferences(world, lastPos);
    }

    private void restorePluginReferences(World world, Coords lastPos) {
        restoreStartLocationAndLayer(lastPos);

        List<Asset> assets = world.getAssets();
        List<Location> locations = world.getLocations();
        List<InventoryPlaceType> inventoryPlaces = world.getInventoryPlaces();
        List<EquipmentType> equipmentTypes = world.getEquipmentTypes();
        List<Dialog> dialogs = world.getDialogs();
        controller.restoreItemsReferences(assets, locations, inventoryPlaces, equipmentTypes, dialogs);
    }

    public void restoreActivePlugin() {
        Model model = controller.getModel();
        Plugin plugin = getLoadedPluginFromMetadata(model);
        if (plugin == null) return;
        model.setActivePlugin(plugin);
        World world = plugin.getWorld();
        Coords startPos = plugin.getStartPos();

        restorePluginReferences(world, startPos);
    }

    private Plugin getLoadedPluginFromMetadata(Model model) {
        PluginMetadata metadata = model.getActivePluginMetadata();
        if (metadata == null) return null;
        String pluginName = metadata.getPluginName();
        PluginCaretaker caretaker = new PluginCaretaker(controller.getProgramDir());
        return caretaker.deserialize(pluginName);
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
        Coords curPos = controller.getPosToCenter();
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

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}