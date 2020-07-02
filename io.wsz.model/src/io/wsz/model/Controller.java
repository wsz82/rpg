package io.wsz.model;

import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Controller {
    private static Controller singleton;
    private static File programDir;
    private final Map<Creature, Location> heroes = new IdentityHashMap<>(6);
    private final List<Creature> creaturesToControl = new ArrayList<>(0);
    private final List<Creature> creaturesToLooseControl = new ArrayList<>(0);
    private final Coords posToCenter = new Coords();
    private Location locationToUpdate;
    private Creature creatureToOpenContainer;
    private Container containerToOpen;
    private PosItem asking;
    private PosItem answering;

    public static Controller get() {
        if (singleton == null) {
            singleton = new Controller();
        }
        return singleton;
    }

    public static File getProgramDir() {
        return programDir;
    }

    public static void setProgramDir(File programDir) {
        Controller.programDir = programDir;
    }

    private Controller(){}

    public void setActivePlugin(Plugin plugin) {
        ActivePlugin.get().setPlugin(plugin);
    }

    public Plugin getActivePlugin() {
        return ActivePlugin.get().getPlugin();
    }

    public void loadAssetsToList() {
        Plugin plugin = ActivePlugin.get().getPlugin();

        AssetsList.get().clear();
        AssetsList.get().addAll(plugin.getAssets());
    }

    public Plugin loadPlugin(String pluginName) {
        if (pluginName == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginName);
    }

    public void removeItem(PosItem pi) {
        CurrentLocation.get().getItems().remove(pi);
    }

    public CurrentLayer getCurrentLayer() {
        return CurrentLayer.get();
    }

    public CurrentLocation getCurrentLocation() {
        return CurrentLocation.get();
    }

    public ObservableList<Location> getLocationsList() {
        return LocationsList.get();
    }

    public List<Asset> getAssetsList() {
        return AssetsList.get();
    }

    public Board getBoard() {
        return Board.get();
    }

    public Coords getPosToCenter() {
        return posToCenter;
    }

    public void setPosToCenter(Coords posToCenter) {
        this.posToCenter.x = posToCenter.x;
        this.posToCenter.y = posToCenter.y;
    }

    public Plugin loadPluginMetadata(String name) {
        PluginCaretaker pc = new PluginCaretaker();
        return pc.getPluginMetadata(name);
    }


    public Location getLocationToUpdate() {
        return locationToUpdate;
    }

    public void setLocationToUpdate(Location locationToUpdate) {
        this.locationToUpdate = locationToUpdate;
    }

    public Coords getBoardPos() {
        return Board.get().getBoardPos();
    }

    public Creature getCreatureToOpenContainer() {
        return creatureToOpenContainer;
    }

    public void setCreatureToOpenContainer(Creature creatureToOpenContainer) {
        this.creatureToOpenContainer = creatureToOpenContainer;
    }

    public void setContainerToOpen(Container container) {
        this.containerToOpen = container;
    }

    public Container getContainerToOpen() {
        return containerToOpen;
    }

    public PosItem getAsking() {
        return asking;
    }

    public void setAsking(PosItem asking) {
        this.asking = asking;
    }

    public PosItem getAnswering() {
        return answering;
    }

    public void setAnswering(PosItem answering) {
        this.answering = answering;
    }


    public Map<Creature, Location> getHeroes() {
        return heroes;
    }

    public void initNewGameHeroes() {
        this.heroes.clear();
        List<Creature> controllables = Controller.get().getBoard().getControllableCreatures();
        Location current = Controller.get().getCurrentLocation().getLocation();
        for (Creature cr : controllables) {
            this.heroes.put(cr, current);
        }
    }

    public List<Creature> getCreaturesToControl() {
        return creaturesToControl;
    }

    public List<Creature> getCreaturesToLooseControl() {
        return creaturesToLooseControl;
    }

    public void initLoadGameHeroes(Map<Creature, Location> heroes) {
        this.heroes.clear();
        this.heroes.putAll(heroes);
    }

    public void clearHeroesPortraits() {
        for (Creature hero : Controller.get().getHeroes().keySet()) {
            hero.setPortrait(null);
        }
    }
}