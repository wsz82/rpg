package game.model;

import game.model.plugin.LastPluginCaretaker;
import game.model.save.SaveCaretaker;
import game.model.save.SaveMemento;
import game.model.setting.SettingCaretaker;
import game.model.setting.SettingMemento;
import game.model.setting.Settings;
import game.model.textures.Cursor;
import game.model.world.GameRunner;
import game.view.menu.GameStage;
import game.view.world.board.GameView;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.list.ItemsList;
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
import javafx.scene.ImageCursor;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController extends Controller {
    private final Cursor cursor = new Cursor();
    private final ObservableList<String> saves = FXCollections.observableArrayList();
    private final AtomicBoolean isGame = new AtomicBoolean();
    private final AtomicBoolean isDialog = new AtomicBoolean();

    private Settings settings;
    private GameView gameView;
    private GameStage gameStage;
    private GameRunner gameRunner;
    private Creature hoveredHero;
    private Equipment dragged;

    public GameController() {
    }

    public boolean startGame(SaveMemento memento) {
        if (model.getActivePluginMetadata() == null) {
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
        LastPluginCaretaker lpc = new LastPluginCaretaker(programDir);
        String lastPluginName = lpc.loadMemento();
        PluginMetadata loadedMetadata = loadPluginMetadata(lastPluginName);
        if (loadedMetadata == null) return;
        model.setActivePluginMetadata(loadedMetadata);
    }

    public void storeLastPluginName(PluginMetadata metadata) {
        LastPluginCaretaker pc = new LastPluginCaretaker(programDir);
        String pluginName = metadata.getPluginName();
        pc.saveMemento(pluginName);
    }


    public SaveMemento loadSaveMemento(String name, File programDir) {
        SaveCaretaker sc = new SaveCaretaker(programDir);
        SaveMemento memento = sc.loadMemento(name);
        getHeroes().addAll(memento.getHeroes());
        DialogMemento dialogMemento = memento.getDialogMemento();
        if (dialogMemento != null) {
            setDialogMemento(dialogMemento);
            setDialog(true);
            restoreDialogPcAndNpc(dialogMemento.getPc(), dialogMemento.getNpc());
        }
        return memento;
    }

    private void restoreDialogPcAndNpc(Creature pc, PosItem<?,?> npc) {
        List<Location> locations = getLocations();
        boolean pcSet = false;
        boolean npcSet = false;
        for (Location l : locations) {
            ItemsList items = l.getItemsList();
            for (Creature cr : items.getCreatures()) {
                if (!pcSet && pc.equals(cr)) {
                    setDialogNpc(cr);
                    pcSet = true;
                } else if (!npcSet && npc.equals(cr)) {
                    setDialogPc(cr);
                }
            }
            for (PosItem<?,?> item : items.getMergedListWithoutCreatures()) {
                if (!npcSet && npc.equals(item)) {
                    setDialogPc(item);
                    npcSet = true;
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
        savedPos.setLocation(getCurrentLocation());
        savedPos.level = getCurrentLayer().getLevel();
        SaveMemento memento = new SaveMemento(name, savedPos, getHeroes(), getDialogMemento());
        memento.setWorld(getActivePlugin().getWorld());
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

    public void restoreSettings() {
        SettingMemento memento = loadSettingsMemento(programDir);
        this.settings = memento.getSettings();
        settings.setMeter(settings.getHorizontalResolution(), this);
        settings.setVerticalMeter(settings.getVerticalResolution(), this);
        Sizes.setResizeWithResolution(memento.isResizeWithResolution(), this);
        setLocale(settings.getLanguage());
    }

    private SettingMemento loadSettingsMemento(File programDir) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        return sc.loadMemento();
    }

    public void setLocale(String locale) {
        if (locale == null || locale.isEmpty()) return;
        Properties defaultProperties = new Properties();
        fillProperties(defaultProperties, Paths.ENGLISH);
        Properties localeProperties = new Properties(defaultProperties);
        fillProperties(localeProperties, locale);
        setLocale(localeProperties);
    }

    private void fillProperties(Properties properties, String locale) {
        String localePath = programDir + Paths.LOCALE_DIR + File.separator + locale + Paths.DOT_PROPERTIES;
        Reader in = null;
        try {
            in = new FileReader(localePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (in == null) return;
        try {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSettings(File programDir, SettingMemento memento) {
        SettingCaretaker sc = new SettingCaretaker(programDir);
        memento.setResizeWithResolution(Sizes.isResizeWithResolution());
        memento.setSettings(settings);
        sc.saveMemento(memento);
    }

    public void restoreSaveMemento(SaveMemento m) {
        Plugin activePlugin = model.getActivePlugin();
        if (activePlugin == null) {
            activePlugin = getLoadedPluginFromMetadata(model);
            model.setActivePlugin(activePlugin);
        }
        if (activePlugin == null) return;
        World world = m.getWorld();
        activePlugin.setWorld(world);

        Coords lastPos = m.getLastPos();
        setCurPos(lastPos);
        restorePluginReferences(world, lastPos);
    }

    private void setCurPos(Coords lastPos) {
        Coords curPos = getCurPos();
        curPos.x = lastPos.x;
        curPos.y = lastPos.y;
        curPos.level = lastPos.level;
    }

    private void restorePluginReferences(World world, Coords lastPos) {
        restoreStartLocationAndLayer(lastPos);

        ItemsList assets = world.getAssets();
        List<Location> locations = world.getLocations();
        restoreItemsReferences(assets, locations, world);
    }

    public void restoreActivePlugin() {
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
        PluginCaretaker caretaker = new PluginCaretaker(programDir);
        return caretaker.deserialize(pluginName);
    }

    private void restoreStartLocationAndLayer(Coords startPos) {
        restoreLocationOfCoords(startPos);
        Location first = startPos.getLocation();
        model.setCurrentLocation(first);

        int serLevel = startPos.level;
        Optional<Layer> optLayer = first.getLayers().stream()
                .filter(l -> l.getLevel() == serLevel)
                .findFirst();
        Layer startLayer = optLayer.orElse(null);
        if (startLayer == null) {
            throw new NullPointerException("Start layer \"" + serLevel + "\" does not exist in start location");
        }
        model.setCurrentLayer(startLayer);
    }

    public void initLoadedGameSettings(SaveMemento memento) {
        Coords curPos = getCurPos();
        Coords loadedPos = memento.getLastPos();
        curPos.x = loadedPos.x;
        curPos.y = loadedPos.y;
    }

    public void initNewGameSettings() {
        Plugin p = getActivePlugin();
        double startX = p.getStartPos().x;
        double startY = p.getStartPos().y;
        Coords curPos = getPosToCenter();
        curPos.x = startX;
        curPos.y = startY;
    }

    public void showLoaderView(Task<String> loader) {
        gameStage.setLoaderViewToCenter(loader);
    }

    public void refreshGame(List<PosItem<?,?>> sortedItems) {
        if (cursor.hasChanged()) {
            setCursor(cursor.getActiveCursorImage());
        }
        gameView.refresh(sortedItems);
    }

    public void setCursor(ImageCursor imageCursor) {
        gameView.getCanvas().getScene().setCursor(imageCursor);
    }

    public GameView getGameView() {
        return gameView;
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
        setDialogMemento(null);
    }

    public Creature getHoveredHero() {
        return hoveredHero;
    }

    public void setHoveredHero(Creature hoveredHero) {
        this.hoveredHero = hoveredHero;
    }

    public Settings getSettings() {
        return settings;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public Equipment getDragged() {
        return dragged;
    }

    public void setDragged(Equipment dragged) {
        this.dragged = dragged;
    }
}