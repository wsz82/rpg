package io.wsz.model;

import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.DialogItem;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.logger.Logger;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginMetadata;
import io.wsz.model.plugin.PluginMetadataCaretaker;
import io.wsz.model.script.Script;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import io.wsz.model.textures.Fog;
import io.wsz.model.world.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {
    protected final Logger logger = new Logger();
    protected final Board board = new Board(this);
    protected final Fog fog = new Fog();
    protected final LinkedList<Creature> heroes = new LinkedList<>();
    protected final List<Creature> creaturesToControl = new ArrayList<>(0);
    protected final List<Creature> creaturesToLooseControl = new ArrayList<>(0);
    protected final Coords posToCenter = new Coords(-1, -1);
    protected final AtomicBoolean isInventory = new AtomicBoolean();

    protected Model model;
    protected File programDir;
    protected Location locationToUpdate;
    protected Creature creatureToOpenInventory;
    protected Container containerToOpen;
    protected DialogMemento dialogMemento;
    protected Properties locale;

    public Controller(){}

    public File getProgramDir() {
        return programDir;
    }

    public void setProgramDir(File programDir) {
        this.programDir = programDir;
    }

    public PluginMetadata loadPluginMetadata(String pluginName) {
        if (pluginName == null) {
            return new PluginMetadata();
        }
        PluginMetadataCaretaker pc = new PluginMetadataCaretaker(programDir);
        return pc.deserialize(pluginName);
    }

    public void restoreItemsReferences(ItemsList assets,
                                       List<Location> locations,
                                       World world) {
        assets.forEach(a -> {
            a.restoreReferences(this, assets, world);
            a.setController(this);
        });
        for (Location l : locations) {
            ItemsList items = l.getItemsList();
            items.forEach(i -> i.restoreReferences(this, assets, world));
        }
    }

    public InventoryPlaceType getReferencedPlaceType(List<InventoryPlaceType> places, InventoryPlaceType serType) {
        if (serType == null) {
            return null;
        }
        Optional<InventoryPlaceType> optType = places.stream()
                .filter(t -> t.getId().equals(serType.getId()))
                .findFirst();
        InventoryPlaceType place = optType.orElse(null);
        if (place == null) {
            throw new NullPointerException("Inventory place \"" + serType.getId() + "\" should be in list of inventory places");
        }
        return place;
    }

    public void restoreLocationOfCoords(Coords pos) {
        Location serLoc = pos.getLocation();
        if (serLoc != null) {
            Optional<Location> optionalLocation = getLocations().stream()
                    .filter(refLoc -> refLoc.getId().equals(serLoc.getId()))
                    .findFirst();
            Location foundLoc = optionalLocation.orElse(null);
            if (foundLoc == null) {
                throw new NullPointerException("Location \"" + serLoc.getId() + "\" should be in locations list");
            }
            pos.setLocation(foundLoc);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public Board getBoard() {
        return board;
    }

    public Coords getPosToCenter() {
        return posToCenter;
    }

    public void setPosToCenter(Coords posToCenter) {
        this.posToCenter.x = posToCenter.x;
        this.posToCenter.y = posToCenter.y;
        this.posToCenter.level = posToCenter.level;
        this.posToCenter.setLocation(posToCenter.getLocation());
    }

    public List<PluginMetadata> getPluginMetadatas() {
        PluginMetadataCaretaker caretaker = new PluginMetadataCaretaker(programDir);
        return caretaker.getMetadatas();
    }


    public Location getLocationToUpdate() {
        return locationToUpdate;
    }

    public void setLocationToUpdate(Location locationToUpdate) {
        this.locationToUpdate = locationToUpdate;
    }

    public Coords getCurPos() {
        return board.getCurPos();
    }

    public Creature getCreatureToOpenInventory() {
        return creatureToOpenInventory;
    }

    public void setCreatureToOpenInventory(Creature creatureToOpenContainer) {
        this.creatureToOpenInventory = creatureToOpenContainer;
    }

    public void setContainerToOpen(Container container) {
        this.containerToOpen = container;
    }

    public Container getContainerToOpen() {
        return containerToOpen;
    }

    public Creature getDialogPc() {
        if (dialogMemento == null) {
            return null;
        } else {
            return dialogMemento.getPc();
        }
    }

    public void setDialogNpc(Creature npc) {
        initDialogMementoIfIsNull();
        this.dialogMemento.setPc(npc);
    }

    public PosItem<?,?> getDialogNpc() {
        if (dialogMemento == null) {
            return null;
        } else {
            return dialogMemento.getNpc();
        }
    }

    public void setDialogPc(PosItem<?,?> answering) {
        initDialogMementoIfIsNull();
        this.dialogMemento.setNpc(answering);
    }

    private void initDialogMementoIfIsNull() {
        if (dialogMemento == null) {
            ArrayList<DialogItem> dialogs = new ArrayList<>(0);
            dialogMemento = new DialogMemento(dialogs);
        }
    }

    public LinkedList<Creature> getHeroes() {
        return heroes;
    }

    public void initNewGameHeroes() {
        this.heroes.clear();
        Location start = model.getCurrentLocation();
        List<Creature> controllables = board.getControllableCreatures(start);
        for (Creature cr : controllables) {
            this.heroes.addLast(cr);
        }
    }

    public List<Creature> getCreaturesToControl() {
        return creaturesToControl;
    }

    public List<Creature> getCreaturesToLooseControl() {
        return creaturesToLooseControl;
    }

    public void initLoadGameHeroes(LinkedList<Creature> heroes) {
        this.heroes.clear();
        this.heroes.addAll(heroes);
    }

    public void reloadInventoryPictures() {
        ItemsList assets = getAssets();
        if (assets == null) return;
        assets.getCreatures().forEach(cr -> {
                    CreatureAnimation anim = cr.getAnimation();
                    anim.clearInventoryPictures();
                });
    }

    public void reloadHeroesPortraits() {
        heroes.forEach(c -> c.getPortraitAnimation().initIdlesOrEquivalent());
    }

    public boolean isInventory() {
        return isInventory.get();
    }

    public void setInventory(boolean isInventory) {
        this.isInventory.set(isInventory);
    }

    public void closeInventory() {
        setInventory(false);
        setContainerToOpen(null);
        setCreatureToOpenInventory(null);
    }

    public DialogMemento getDialogMemento() {
        return dialogMemento;
    }

    public void setDialogMemento(DialogMemento dialogMemento) {
        this.dialogMemento = dialogMemento;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public List<Location> getLocations() {
        Plugin activePlugin = model.getActivePlugin();
        if (activePlugin == null) return null;
        return activePlugin.getWorld().getLocations();
    }

    public ItemsList getAssets() {
        Plugin activePlugin = model.getActivePlugin();
        if (activePlugin == null) return null;
        return activePlugin.getWorld().getAssets();
    }

    public Script getScriptById(String id) {
        return model.getActivePlugin().getWorld().getScripts().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Variable<?> getGlobalVariableById(String id) {
        World world = model.getActivePlugin().getWorld();
        return world.getVariables().getVariableById(id);
    }

    public Asset<?> getAssetById(String id) {
        return getAssets().getMergedList().stream()
                .filter(a -> a.getAssetId().equals(id))
                .findFirst().orElse(null);
    }

    public PosItem<?,?> getItemByAssetId(String assetId) {
        for (Location l : getLocations()) {
            for (PosItem<?,?> i : l.getItemsList().getMergedList()) {
                PosItem<?,?> item = i.getItemByAssetId(assetId);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    public PosItem<?,?> getItemOrAssetById(String id) {
        PosItem<?,?> item = getItemByItemId(id);
        if (item == null) {
            item = getItemByAssetId(id);
        }
        return item;
    }

    public PosItem<?,?> getItemByItemId(String itemId) {
        for (Location l : getLocations()) {
            for (PosItem<?,?> i : l.getItemsList().getMergedList()) {
                PosItem<?,?> item = i.getItemByItemId(itemId);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    public Location getLocationById(String id) {
        return model.getActivePlugin().getWorld().getLocations().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst().orElse(null);
    }

    public InventoryPlaceType getInventoryPlaceById(String id) {
        return model.getActivePlugin().getWorld().getInventoryPlaces().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst().orElse(null);
    }

    public Plugin getActivePlugin() {
        return model.getActivePlugin();
    }

    public void initNewModel() {
        model = new Model();
    }

    public Fog getFog() {
        return fog;
    }

    public Location getCurrentLocation() {
        return model.getCurrentLocation();
    }

    public void setCurrentLocation(Location location) {
        model.setCurrentLocation(location);
    }

    public Layer getCurrentLayer() {
        return model.getCurrentLayer();
    }

    public void setCurrentLayer(Layer layer) {
        model.setCurrentLayer(layer);
    }

    public Properties getLocale() {
        return locale;
    }

    public void setLocale(Properties locale) {
        this.locale = locale;
    }
}